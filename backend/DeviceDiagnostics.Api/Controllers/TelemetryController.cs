using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[ApiController]
[Route("devices/{deviceId:int}/telemetry")]
public class TelemetryController : ControllerBase
{
    private readonly AppDbContext _db;

    public TelemetryController(AppDbContext db) => _db = db;

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(Telemetry))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<Telemetry>> Create(int deviceId, [FromBody] CreateTelemetryRequest request, CancellationToken ct)
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

        return Created($"/devices/{deviceId}/telemetry/{telemetry.Id}", telemetry);
    }

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<Telemetry>))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<List<Telemetry>>> Get(int deviceId, [FromQuery] DateTime? fromUtc, [FromQuery] DateTime? toUtc,[FromQuery] string? metric, CancellationToken ct)
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
            .ToListAsync(ct);

        return Ok(items);
    }
}
