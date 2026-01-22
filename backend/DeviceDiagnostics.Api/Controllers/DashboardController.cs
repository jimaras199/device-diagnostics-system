using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[ApiController]
[Route("dashboard")]
public class DashboardController : ControllerBase
{
    private readonly AppDbContext _db;

    public DashboardController(AppDbContext db) => _db = db;

    [HttpGet("devices")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<DeviceDashboardItem>))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    public async Task<ActionResult<List<DeviceDashboardItem>>> GetDevicesDashboard([FromQuery] int metricsPerDevice = 5, CancellationToken ct = default)
    {
        if (metricsPerDevice < 0 || metricsPerDevice > 20)
        {
            ModelState.AddModelError(nameof(metricsPerDevice), "metricsPerDevice must be between 0 and 20.");
            return ValidationProblem(ModelState);
        }
        
        var devices = await _db.Devices
            .AsNoTracking()
            .OrderByDescending(d => d.LastSeen)
            .Select(d => new DeviceDashboardItem
            {
                Id = d.Id,
                Name = d.Name,
                Model = d.Model,
                LastSeenUtc = d.LastSeen,
                LatestMetrics = new List<MetricSnapshot>()
            })
            .ToListAsync(ct);

        if (devices.Count == 0 || metricsPerDevice == 0)
            return Ok(devices);

        var deviceIds = devices.Select(d => d.Id).ToList();

        var recentTelemetry = await _db.Telemetries
            .AsNoTracking()
            .Where(t => deviceIds.Contains(t.DeviceId))
            .OrderByDescending(t => t.Timestamp)
            .Take(2000)
            .Select(t => new
            {
                t.DeviceId,
                t.MetricName,
                t.Value,
                t.Timestamp
            })
            .ToListAsync(ct);

        var grouped = recentTelemetry
            .GroupBy(x => x.DeviceId)
            .ToDictionary(
                g => g.Key,
                g => g
                    .GroupBy(x => x.MetricName)
                    .Select(mg => mg.OrderByDescending(x => x.Timestamp).First())
                    .OrderByDescending(x => x.Timestamp)
                    .Take(metricsPerDevice)
                    .Select(x => new MetricSnapshot
                    {
                        MetricName = x.MetricName,
                        Value = x.Value,
                        TimestampUtc = x.Timestamp
                    })
                    .ToList()
            );

        foreach (var d in devices)
        {
            if (grouped.TryGetValue(d.Id, out var metrics))
                d.LatestMetrics = metrics;
        }

        return Ok(devices);
    }
}
