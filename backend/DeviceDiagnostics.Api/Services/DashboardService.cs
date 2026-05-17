using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Services;

public class DashboardService
{
    private readonly AppDbContext _db;

    public DashboardService(AppDbContext db)
    {
        _db = db;
    }

    public async Task<List<DeviceDashboardItem>> GetDevicesDashboardAsync(
        int userId,
        int metricsPerDevice,
        CancellationToken ct)
    {
        var devices = await _db.Devices
            .AsNoTracking()
            .Where(d => d.OwnerUserId == userId)
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
            return devices;

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

        return devices;
    }
}