using System.ComponentModel.DataAnnotations;

namespace DeviceDiagnostics.Api.Contracts;

public class CreateTelemetryRequest
{
    [Required]
    [MinLength(1)]
    public string MetricName { get; set; } = "";

    public double Value { get; set; }

    public DateTime? TimestampUtc { get; set; }
}