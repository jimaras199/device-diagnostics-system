using DeviceDiagnostics.Api.Contracts;
using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Services;

public class DevicesService
{
    private readonly AppDbContext _db;

    public DevicesService(AppDbContext db)
    {
        _db = db;
    }

    public async Task<List<DeviceResponse>> GetDevicesAsync(int userId, CancellationToken ct)
    {
        return await _db.Devices
            .AsNoTracking()
            .Where(d => d.OwnerUserId == userId)
            .OrderByDescending(d => d.LastSeen)
            .Select(d => d.ToResponse())
            .ToListAsync(ct);
    }

    public async Task<DeviceResponse?> GetDeviceByIdAsync(int userId, int id, CancellationToken ct)
    {
        return await _db.Devices
            .AsNoTracking()
            .Where(d => d.Id == id && d.OwnerUserId == userId)
            .Select(d => d.ToResponse())
            .FirstOrDefaultAsync(ct);
    }

    public async Task<DeviceResponse> CreateDeviceAsync(
        int userId,
        CreateDeviceRequest request,
        CancellationToken ct)
    {
        var device = new Device
        {
            OwnerUserId = userId,
            Name = request.Name.Trim(),
            Model = string.IsNullOrWhiteSpace(request.Model) ? null : request.Model.Trim(),
            LastSeen = DateTime.UtcNow
        };

        _db.Devices.Add(device);
        await _db.SaveChangesAsync(ct);

        return device.ToResponse();
    }
}