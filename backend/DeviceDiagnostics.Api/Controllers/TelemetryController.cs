using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using DeviceDiagnostics.Api.Contracts.Responses;


namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("devices/{deviceId:int}/telemetry")]
public class TelemetryController : ControllerBase
{
    private readonly AppDbContext _db;

    public TelemetryController(AppDbContext db) => _db = db;

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(TelemetryResponse))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<TelemetryResponse>> Create(int deviceId, [FromBody] CreateTelemetryRequest request, CancellationToken ct)
    {
        var deviceExists = await _db.Devices.AnyAsync(d => d.Id == deviceId, ct);
        if (!deviceExists)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var telemetry = new Telemetry
        {
            DeviceId = deviceId,
            MetricName = request.MetricName.Trim(),
            Value = request.Value,
            Timestamp = request.TimestampUtc ?? DateTime.UtcNow
        };

        _db.Telemetries.Add(telemetry);
        await _db.SaveChangesAsync(ct);

        var response = new TelemetryResponse
        {
            Id = telemetry.Id,
            MetricName = telemetry.MetricName,
            Value = telemetry.Value,
            TimestampUtc = telemetry.Timestamp
        };

        return Created($"/devices/{deviceId}/telemetry/{telemetry.Id}", response);
    }

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<TelemetryResponse>))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<List<TelemetryResponse>>> Get(int deviceId, [FromQuery] DateTime? fromUtc, [FromQuery] DateTime? toUtc,[FromQuery] string? metric, CancellationToken ct)
    {
        var deviceExists = await _db.Devices.AnyAsync(d => d.Id == deviceId, ct);
        if (!deviceExists)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var query = _db.Telemetries.AsNoTracking()
            .Where(t => t.DeviceId == deviceId);

        if (!string.IsNullOrWhiteSpace(metric))
            query = query.Where(t => t.MetricName == metric.Trim());

        if (fromUtc is not null)
            query = query.Where(t => t.Timestamp >= fromUtc.Value);

        if (toUtc is not null)
            query = query.Where(t => t.Timestamp <= toUtc.Value);

        var items = await query
            .OrderByDescending(t => t.Timestamp)
            .Take(200)
            .Select(t => new TelemetryResponse
            {
                Id = t.Id,
                MetricName = t.MetricName,
                Value = t.Value,
                TimestampUtc = t.Timestamp
            })
            .ToListAsync(ct);

        return Ok(items);
    }
}
