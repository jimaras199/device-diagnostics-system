using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Services;

public class TelemetryService
{
    private readonly AppDbContext _db;

    public TelemetryService(AppDbContext db)
    {
        _db = db;
    }

    public async Task<bool> DeviceBelongsToUserAsync(
        int userId,
        int deviceId,
        CancellationToken ct)
    {
        return await _db.Devices
            .AsNoTracking()
            .AnyAsync(d => d.Id == deviceId && d.OwnerUserId == userId, ct);
    }

    public async Task<TelemetryResponse> CreateTelemetryAsync(
        int userId,
        int deviceId,
        CreateTelemetryRequest request,
        CancellationToken ct)
    {
        var telemetry = new Telemetry
        {
            DeviceId = deviceId,
            MetricName = request.MetricName.Trim(),
            Value = request.Value,
            Timestamp = request.TimestampUtc ?? DateTime.UtcNow
        };

        _db.Telemetries.Add(telemetry);

        await _db.Devices
            .Where(d => d.Id == deviceId && d.OwnerUserId == userId)
            .ExecuteUpdateAsync(
                s => s.SetProperty(d => d.LastSeen, _ => DateTime.UtcNow),
                ct
            );

        await _db.SaveChangesAsync(ct);

        return new TelemetryResponse
        {
            Id = telemetry.Id,
            MetricName = telemetry.MetricName,
            Value = telemetry.Value,
            TimestampUtc = telemetry.Timestamp
        };
    }

    public async Task<List<TelemetryResponse>> GetTelemetryAsync(
        int userId,
        int deviceId,
        DateTime? fromUtc,
        DateTime? toUtc,
        string? metric,
        CancellationToken ct)
    {
        var query = _db.Telemetries
            .AsNoTracking()
            .Where(t => t.DeviceId == deviceId);

        if (!string.IsNullOrWhiteSpace(metric))
            query = query.Where(t => t.MetricName == metric.Trim());

        if (fromUtc is not null)
            query = query.Where(t => t.Timestamp >= fromUtc.Value);

        if (toUtc is not null)
            query = query.Where(t => t.Timestamp <= toUtc.Value);

        return await query
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
    }
}