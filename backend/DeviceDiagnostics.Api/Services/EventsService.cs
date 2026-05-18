using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Services;

public class EventsService
{
    private readonly AppDbContext _db;
    private readonly DevicesService _devicesService;

    public EventsService(AppDbContext db, DevicesService devicesService)
    {
        _db = db;
        _devicesService = devicesService;
    }

    

    public async Task<EventResponse> CreateEventAsync(
        int userId,
        int deviceId,
        CreateEventRequest request,
        CancellationToken ct)
    {
        var ev = new EventLog
        {
            DeviceId = deviceId,
            Level = request.Level.Trim(),
            Message = request.Message.Trim(),
            Timestamp = request.TimestampUtc ?? DateTime.UtcNow
        };

        _db.EventLogs.Add(ev);

        await _db.Devices
            .Where(d => d.Id == deviceId && d.OwnerUserId == userId)
            .ExecuteUpdateAsync(
                s => s.SetProperty(d => d.LastSeen, _ => DateTime.UtcNow),
                ct
            );

        await _db.SaveChangesAsync(ct);

        return new EventResponse
        {
            Id = ev.Id,
            Level = ev.Level,
            Message = ev.Message,
            TimestampUtc = ev.Timestamp
        };
    }

    public async Task<List<EventResponse>> GetEventsAsync(
        int userId,
        int deviceId,
        CancellationToken ct)
    {
        return await _db.EventLogs
            .AsNoTracking()
            .Where(e => e.DeviceId == deviceId)
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
    }
}