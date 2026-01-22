using DeviceDiagnostics.Api.Domain;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Infrastructure;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<Device> Devices => Set<Device>();
    public DbSet<Telemetry> Telemetries => Set<Telemetry>();
    public DbSet<EventLog> EventLogs => Set<EventLog>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Telemetry>()
            .HasOne(t => t.Device)
            .WithMany()
            .HasForeignKey(t => t.DeviceId);

        modelBuilder.Entity<EventLog>()
            .HasOne(e => e.Device)
            .WithMany()
            .HasForeignKey(e => e.DeviceId);

        modelBuilder.Entity<Telemetry>().HasIndex(t => new { t.DeviceId, t.Timestamp });
    }
}
