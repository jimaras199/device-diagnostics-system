using System.Security.Claims;

namespace DeviceDiagnostics.Api.Infrastructure;

public static class UserClaimsExtensions
{
    public static int GetUserId(this ClaimsPrincipal user)
    {
        var uid = user.FindFirstValue("uid")
                  ?? user.FindFirstValue(ClaimTypes.NameIdentifier)
                  ?? user.FindFirstValue(ClaimTypes.Name);

        if (string.IsNullOrWhiteSpace(uid) || !int.TryParse(uid, out var id))
            throw new InvalidOperationException("User id claim missing or invalid.");

        return id;
    }
}