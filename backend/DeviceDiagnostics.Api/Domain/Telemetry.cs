namespace DeviceDiagnostics.Api.Domain;

public class Telemetry
{
    public int Id { get; set; }

    public int DeviceId { get; set; }
    public Device? Device { get; set; }

    public DateTime Timestamp { get; set; } = DateTime.UtcNow;
    public string MetricName { get; set; } = "";
    public double Value { get; set; }
}
