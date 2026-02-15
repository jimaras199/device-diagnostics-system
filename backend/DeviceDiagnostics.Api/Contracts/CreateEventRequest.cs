using System.ComponentModel.DataAnnotations;

namespace DeviceDiagnostics.Api.Contracts;

public class CreateEventRequest
{
    [Required]
    [MinLength(1)]
    public string Level { get; set; } = "Info";

    [Required]
    [MinLength(1)]
    public string Message { get; set; } = "";

    public DateTime? TimestampUtc { get; set; }
}
