namespace DeviceDiagnostics.Api.Contracts.Responses;

public class DeviceDashboardItem
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public string? Model { get; set; }
    public DateTime LastSeenUtc { get; set; }
    public List<MetricSnapshot> LatestMetrics { get; set; } = new();
}

public class MetricSnapshot
{
    public string MetricName { get; set; } = "";
    public double Value { get; set; }
    public DateTime TimestampUtc { get; set; }
}
