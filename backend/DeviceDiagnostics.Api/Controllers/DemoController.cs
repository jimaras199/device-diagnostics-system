using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Infrastructure;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("users/me/demo")]
public class DemoController : ControllerBase
{
    private readonly DemoSeeder _seeder;
    private readonly AppDbContext _db;

    public DemoController(DemoSeeder seeder, AppDbContext db)
    {
        _seeder = seeder;
        _db = db;
    }

    [HttpGet("status")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(DemoStatusResponse))]
    public async Task<ActionResult<DemoStatusResponse>> Status(CancellationToken ct)
    {
        var userId = User.GetUserId();

        var seeded = await _db.Devices
            .AsNoTracking()
            .AnyAsync(d => d.OwnerUserId == userId && d.IsDemo, ct);

        return Ok(new DemoStatusResponse { Seeded = seeded });
    }

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