using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("users/me/demo")]
public class DemoController : ControllerBase
{
    private readonly DemoSeeder _seeder;

    public DemoController(DemoSeeder seeder) => _seeder = seeder;

    [HttpPost("seed")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(DemoSeedResponse))]
    [ProducesResponseType(StatusCodes.Status409Conflict, Type = typeof(ProblemDetails))]
    public async Task<ActionResult<DemoSeedResponse>> Seed(CancellationToken ct)
    {
        var userId = User.GetUserId();

        var (alreadyLoaded, result) = await _seeder.SeedForUserAsync(userId, ct);

        if (alreadyLoaded)
            return Conflict(ApiErrors.Conflict("Demo already loaded."));

        return Ok(result!);
    }
}
