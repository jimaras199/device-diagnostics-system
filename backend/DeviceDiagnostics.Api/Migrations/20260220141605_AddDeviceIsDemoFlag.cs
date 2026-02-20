using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace DeviceDiagnostics.Api.Migrations
{
    /// <inheritdoc />
    public partial class AddDeviceIsDemoFlag : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "IsDemo",
                table: "Devices",
                type: "INTEGER",
                nullable: false,
                defaultValue: false);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsDemo",
                table: "Devices");
        }
    }
}
