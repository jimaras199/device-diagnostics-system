using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[ApiController]
[Authorize]
[Route("devices/{deviceId:int}/events")]
public class EventsController : ControllerBase
{
    private readonly AppDbContext _db;

    public EventsController(AppDbContext db) => _db = db;

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(EventResponse))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<EventResponse>> Create(int deviceId,[FromBody] CreateEventRequest request,CancellationToken ct)
    {
        var deviceExists = await _db.Devices.AnyAsync(d => d.Id == deviceId, ct);
        if (!deviceExists)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var level = request.Level.Trim();
        var message = request.Message.Trim();

        if (string.IsNullOrWhiteSpace(level))
        {
            ModelState.AddModelError(nameof(request.Level), "Level is required.");
            return ValidationProblem(ModelState);
        }

        if (string.IsNullOrWhiteSpace(message))
        {
            ModelState.AddModelError(nameof(request.Message), "Message is required.");
            return ValidationProblem(ModelState);
        }

        var ev = new EventLog
        {
            DeviceId = deviceId,
            Level = level,
            Message = message,
            Timestamp = request.TimestampUtc ?? DateTime.UtcNow
        };

        _db.EventLogs.Add(ev);
        await _db.SaveChangesAsync(ct);

        var response = new EventResponse
        {
            Id = ev.Id,
            Level = ev.Level,
            Message = ev.Message,
            TimestampUtc = ev.Timestamp
        };

        return Created($"/devices/{deviceId}/events/{ev.Id}", response);
    }

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<EventResponse>))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<List<EventResponse>>> Get(int deviceId,[FromQuery] DateTime? fromUtc,[FromQuery] DateTime? toUtc,[FromQuery] string? level,CancellationToken ct)
    {
        var deviceExists = await _db.Devices.AnyAsync(d => d.Id == deviceId, ct);
        if (!deviceExists)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var query = _db.EventLogs
            .AsNoTracking()
            .Where(e => e.DeviceId == deviceId);

        if (!string.IsNullOrWhiteSpace(level))
            query = query.Where(e => e.Level == level.Trim());

        if (fromUtc is not null)
            query = query.Where(e => e.Timestamp >= fromUtc.Value);

        if (toUtc is not null)
            query = query.Where(e => e.Timestamp <= toUtc.Value);

        var items = await query
            .OrderByDescending(e => e.Timestamp)
            .Take(200)
            .Select(e => new EventResponse
            {
                Id = e.Id,
                Level = e.Level,
                Message = e.Message,
                TimestampUtc = e.Timestamp
            })
            .ToListAsync(ct);
        return Ok(items);
    }
}
