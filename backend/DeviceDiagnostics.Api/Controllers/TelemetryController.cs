using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using DeviceDiagnostics.Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("devices/{deviceId:int}/telemetry")]
public class TelemetryController : ControllerBase
{
    private readonly TelemetryService _telemetryService;
    private readonly DevicesService _devicesService;

    public TelemetryController(TelemetryService telemetryService, DevicesService devicesService)
    {
        _telemetryService = telemetryService;
        _devicesService = devicesService;
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created, Type = typeof(TelemetryResponse))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<TelemetryResponse>> Create(int deviceId, [FromBody] CreateTelemetryRequest request, CancellationToken ct)
    {
        var userId = User.GetUserId();

        var deviceOwned = await _devicesService.DeviceExistsForUserAsync(userId, deviceId, ct);

        if (!deviceOwned)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var telemetry = new Telemetry
        {
            DeviceId = deviceId,
            MetricName = request.MetricName.Trim(),
            Value = request.Value,
            Timestamp = request.TimestampUtc ?? DateTime.UtcNow
        };

        var response = await _telemetryService.CreateTelemetryAsync(userId, deviceId, request, ct);

        return Created($"/devices/{deviceId}/telemetry/{telemetry.Id}", response);
    }

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<TelemetryResponse>))]
    [ProducesResponseType(StatusCodes.Status404NotFound, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<List<TelemetryResponse>>> Get(int deviceId, [FromQuery] DateTime? fromUtc, [FromQuery] DateTime? toUtc,[FromQuery] string? metric, CancellationToken ct)
    {
        var userId = User.GetUserId();

        var deviceOwned = await _devicesService.DeviceExistsForUserAsync(userId, deviceId, ct);

        if (!deviceOwned)
            return NotFound(ApiErrors.NotFound($"Device {deviceId} was not found."));

        var items = await _telemetryService.GetTelemetryAsync(
        userId,
        deviceId,
        fromUtc,
        toUtc,
        metric,
        ct);

        return Ok(items);
    }
}
