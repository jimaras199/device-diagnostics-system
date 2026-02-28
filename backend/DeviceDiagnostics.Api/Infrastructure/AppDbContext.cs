using DeviceDiagnostics.Api.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

namespace DeviceDiagnostics.Api.Infrastructure;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }
    public DbSet<Device> Devices => Set<Device>();
    public DbSet<Telemetry> Telemetries => Set<Telemetry>();
    public DbSet<EventLog> EventLogs => Set<EventLog>();
    public DbSet<AppUser> Users => Set<AppUser>();
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        var utcConverter = new ValueConverter<DateTime, DateTime>(
            v => v.Kind == DateTimeKind.Utc ? v : DateTime.SpecifyKind(v, DateTimeKind.Utc),
            v => DateTime.SpecifyKind(v, DateTimeKind.Utc)
        );

        modelBuilder.Entity<AppUser>()
            .HasIndex(u => u.Email)
            .IsUnique();

        modelBuilder.Entity<Device>()
            .HasIndex(d => new { d.OwnerUserId, d.LastSeen });

        modelBuilder.Entity<Telemetry>()
            .HasOne(t => t.Device)
            .WithMany()
            .HasForeignKey(e => e.DeviceId);

        modelBuilder.Entity<EventLog>()
            .HasOne(e => e.Device)
            .WithMany()
            .HasForeignKey(e => e.DeviceId);

        modelBuilder.Entity<Telemetry>()
            .HasIndex(t => new { t.DeviceId, t.Timestamp });

        modelBuilder.Entity<Device>()
            .Property(d => d.LastSeen)
            .HasConversion(utcConverter);

        modelBuilder.Entity<Telemetry>()
            .Property(t => t.Timestamp)
            .HasConversion(utcConverter);

        modelBuilder.Entity<EventLog>()
            .Property(e => e.Timestamp)
            .HasConversion(utcConverter);

        modelBuilder.Entity<AppUser>()
            .Property(u => u.CreatedUtc)
            .HasConversion(utcConverter);
    }
}