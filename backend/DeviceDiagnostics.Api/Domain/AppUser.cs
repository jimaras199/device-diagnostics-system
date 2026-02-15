namespace DeviceDiagnostics.Api.Domain;

public class AppUser
{
    public int Id { get; set; }
    public string Email { get; set; } = "";
    public string PasswordHash { get; set; } = "";
    public DateTime CreatedUtc { get; set; } = DateTime.UtcNow;
}
