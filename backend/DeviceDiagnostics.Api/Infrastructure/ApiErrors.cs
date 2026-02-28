using Microsoft.AspNetCore.Mvc;

namespace DeviceDiagnostics.Api.Infrastructure;

public static class ApiErrors
{
    public static ProblemDetails NotFound(string detail) => new()
    {
        Title = "Resource not found",
        Status = StatusCodes.Status404NotFound,
        Detail = detail
    };

    public static ProblemDetails Conflict(string detail) => new()
    {
        Title = "Conflict",
        Status = StatusCodes.Status409Conflict,
        Detail = detail
    };
    public static ProblemDetails Unauthorized(string detail) => new()
    {
        Title = "Unauthorized",
        Status = StatusCodes.Status401Unauthorized,
        Detail = detail
    };
}
