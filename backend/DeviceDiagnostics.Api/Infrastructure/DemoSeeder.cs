using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Domain;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Infrastructure;

public class DemoSeeder
{
    private readonly AppDbContext _db;

    public DemoSeeder(AppDbContext db) => _db = db;

    public async Task<(bool alreadyLoaded, DemoSeedResponse? result)> SeedForUserAsync(int userId, CancellationToken ct)
    {
        var already = await _db.Devices.AsNoTracking()
            .AnyAsync(d => d.OwnerUserId == userId && d.IsDemo, ct);

        if (already)
            return (true, null);

        var rnd = new Random();

        int deviceCount = rnd.Next(3, 6); 
        var now = DateTime.UtcNow;

        var devices = new List<Device>();
        var telemetries = new List<Telemetry>();
        var events = new List<EventLog>();

        
        var models = new[] { "QD-100", "QD-200", "QD-Pro", "QD-Lite" };
        var metricNames = new[] { "battery_pct", "temp_c", "signal_dbm", "cpu_pct" };
        var levels = new[] { "Info", "Warning", "Error" };

        for (int i = 1; i <= deviceCount; i++)
        {
            var d = new Device
            {
                OwnerUserId = userId,
                IsDemo = true,
                Name = $"Demo Device {i}",
                Model = models[rnd.Next(models.Length)],
                LastSeen = now 
            };
            devices.Add(d);
        }

        _db.Devices.AddRange(devices);
        await _db.SaveChangesAsync(ct); 

        foreach (var d in devices)
        {
            int telemetryCount = rnd.Next(10, 31); 
            int eventsCount = rnd.Next(5, 16);     

            DateTime latest = DateTime.MinValue;

            for (int t = 0; t < telemetryCount; t++)
            {
                var hoursBack = rnd.Next(24, 73);
                var minutesJitter = rnd.Next(0, 60);
                var ts = now.AddHours(-hoursBack).AddMinutes(-minutesJitter);

                var metric = metricNames[rnd.Next(metricNames.Length)];
                var value = metric switch
                {
                    "battery_pct" => rnd.Next(10, 100),
                    "temp_c" => Math.Round(20 + rnd.NextDouble() * 25, 1),     
                    "signal_dbm" => -1 * rnd.Next(50, 120),                    
                    "cpu_pct" => rnd.Next(1, 100),
                    _ => rnd.NextDouble() * 100
                };

                telemetries.Add(new Telemetry
                {
                    DeviceId = d.Id,
                    MetricName = metric,
                    Value = Convert.ToDouble(value),
                    Timestamp = ts
                });

                if (ts > latest) latest = ts;
            }

            for (int e = 0; e < eventsCount; e++)
            {
                var hoursBack = rnd.Next(24, 73);
                var minutesJitter = rnd.Next(0, 60);
                var ts = now.AddHours(-hoursBack).AddMinutes(-minutesJitter);

                var level = levels[rnd.Next(levels.Length)];
                var message = level switch
                {
                    "Error" => "Critical fault detected",
                    "Warning" => "High temperature warning",
                    _ => "Device heartbeat"
                };

                events.Add(new EventLog
                {
                    DeviceId = d.Id,
                    Level = level,
                    Message = message,
                    Timestamp = ts
                });

                if (ts > latest) latest = ts;
            }

            d.LastSeen = latest == DateTime.MinValue ? now : latest;
        }

        _db.Telemetries.AddRange(telemetries);
        _db.EventLogs.AddRange(events);

        await _db.SaveChangesAsync(ct);

        return (false, new DemoSeedResponse
        {
            DevicesCreated = devices.Count,
            TelemetryCreated = telemetries.Count,
            EventsCreated = events.Count
        });
    }
}
