using DeviceDiagnostics.Api.Contracts.Responses;
using DeviceDiagnostics.Api.Infrastructure;
using DeviceDiagnostics.Api.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DeviceDiagnostics.Api.Controllers;

[Authorize]
[ApiController]
[Route("dashboard")]
public class DashboardController : ControllerBase
{
    private readonly DashboardService _dashboardService;

    public DashboardController(DashboardService dashboardService)
    {
        _dashboardService = dashboardService;
    }

    [HttpGet("devices")]
    [ProducesResponseType(StatusCodes.Status200OK, Type = typeof(List<DeviceDashboardItem>))]
    [ProducesResponseType(StatusCodes.Status400BadRequest, Type = typeof(ValidationProblemDetails))]
    public async Task<ActionResult<List<DeviceDashboardItem>>> GetDevicesDashboard([FromQuery] int metricsPerDevice = 5, CancellationToken ct = default)
    {
        if (metricsPerDevice < 0 || metricsPerDevice > 20)
        {
            ModelState.AddModelError(nameof(metricsPerDevice), "metricsPerDevice must be between 0 and 20.");
            return ValidationProblem(ModelState);
        }

        var userId = User.GetUserId();

        var devices = await _dashboardService.GetDevicesDashboardAsync(
            userId,
            metricsPerDevice,
            ct);

        return Ok(devices);
    }
}
