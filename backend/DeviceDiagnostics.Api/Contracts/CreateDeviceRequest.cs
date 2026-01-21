namespace DeviceDiagnostics.Api.Contracts;
using System.ComponentModel.DataAnnotations;

public class CreateDeviceRequest
{
    [Required]
    public string Name { get; set; } = "";
    public string? Model { get; set; }
}