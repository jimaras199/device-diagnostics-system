namespace DeviceDiagnostics.Api.Contracts.Responses;

public class TelemetryResponse
{
    public int Id { get; set; }
    public string MetricName { get; set; } = "";
    public double Value { get; set; }
    public DateTime TimestampUtc { get; set; }
}
