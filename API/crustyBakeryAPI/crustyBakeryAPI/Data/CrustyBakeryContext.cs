using Microsoft.EntityFrameworkCore;
using crustyBakeryAPI.Models;

namespace crustyBakeryAPI.Data
{
    public class CrustyBakeryContext : DbContext
    {
        public CrustyBakeryContext(DbContextOptions<CrustyBakeryContext> options) : base(options) { }

        public DbSet<Usuario> Usuarios => Set<Usuario>();
        public DbSet<Cliente> Clientes => Set<Cliente>();
        public DbSet<Categoria> Categorias => Set<Categoria>();
        public DbSet<Producto> Productos => Set<Producto>();
        public DbSet<Pedido> Pedidos => Set<Pedido>();
        public DbSet<DetallePedido> DetallesPedido => Set<DetallePedido>();
        public DbSet<Pago> Pagos => Set<Pago>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // ==========================================
            // USUARIO
            // ==========================================
            modelBuilder.Entity<Usuario>()
                .Property(u => u.Rol)
                .HasConversion<string>()
                .HasMaxLength(20);


            // ==========================================
            // PEDIDO
            // ==========================================
            modelBuilder.Entity<Pedido>()
                .Property(p => p.Estado)
                .HasConversion<string>()
                .HasMaxLength(20);

            // La tabla PEDIDO tiene un trigger
            modelBuilder.Entity<Pedido>()
                .ToTable("pedido", tb => tb.HasTrigger("trg_pedido_after_update"));


            // ==========================================
            // PAGO
            // ==========================================
            modelBuilder.Entity<Pago>()
                .Property(p => p.MetodoPago)
                .HasConversion<string>()
                .HasMaxLength(20);

            modelBuilder.Entity<Pago>()
                .Property(p => p.Estado)
                .HasConversion<string>()
                .HasMaxLength(20);

            // La tabla PAGO tiene triggers
            modelBuilder.Entity<Pago>()
                .ToTable("pago", tb => tb.HasTrigger("trg_pago_after_insert"));

            // ==========================================
            // PRODUCTO
            // ==========================================

            // La tabla PRODUCTO tiene triggers
            modelBuilder.Entity<Producto>()
                .ToTable("producto", tb => tb.HasTrigger("trg_producto_after_insert"));


            // ==========================================
            // DETALLE_PEDIDO
            // ==========================================

            // La tabla DETALLE_PEDIDO tiene triggers
            modelBuilder.Entity<DetallePedido>()
                .ToTable("detalle_pedido", tb => tb.HasTrigger("trg_detalle_after_insert"));


            // ==========================================
            // RELACIÓN PEDIDO - CLIENTE
            // ==========================================
            modelBuilder.Entity<Pedido>()
                .HasOne(p => p.Cliente)
                .WithMany(c => c.Pedidos)
                .HasForeignKey(p => p.IdCliente)
                .OnDelete(DeleteBehavior.Restrict);


            // ==========================================
            // RELACIÓN PEDIDO - EMPLEADO
            // ==========================================
            modelBuilder.Entity<Pedido>()
                .HasOne(p => p.Empleado)
                .WithMany(u => u.PedidosGestionados)
                .HasForeignKey(p => p.IdUsuarioEmpleado)
                .OnDelete(DeleteBehavior.SetNull);


            // ==========================================
            // RELACIÓN PEDIDO - REPOSTERO
            // ==========================================
            modelBuilder.Entity<Pedido>()
                .HasOne(p => p.Repostero)
                .WithMany(u => u.PedidosPreparados)
                .HasForeignKey(p => p.IdUsuarioRepostero)
                .OnDelete(DeleteBehavior.SetNull);


            // ==========================================
            // RELACIÓN PRODUCTO - CATEGORIA
            // ==========================================
            modelBuilder.Entity<Producto>()
                .HasOne(pr => pr.Categoria)
                .WithMany(c => c.Productos)
                .HasForeignKey(pr => pr.IdCategoria)
                .OnDelete(DeleteBehavior.Restrict);


            // ==========================================
            // RELACIÓN DETALLE_PEDIDO - PEDIDO
            // ==========================================
            modelBuilder.Entity<DetallePedido>()
                .HasOne(d => d.Pedido)
                .WithMany(p => p.Detalles)
                .HasForeignKey(d => d.IdPedido)
                .OnDelete(DeleteBehavior.Cascade);


            // ==========================================
            // RELACIÓN DETALLE_PEDIDO - PRODUCTO
            // ==========================================
            modelBuilder.Entity<DetallePedido>()
                .HasOne(d => d.Producto)
                .WithMany(p => p.DetallesPedido)
                .HasForeignKey(d => d.IdProducto)
                .OnDelete(DeleteBehavior.Restrict);


            // ==========================================
            // RELACIÓN PAGO - PEDIDO
            // ==========================================
            modelBuilder.Entity<Pago>()
                .HasOne(pa => pa.Pedido)
                .WithOne(p => p.Pago)
                .HasForeignKey<Pago>(pa => pa.IdPedido)
                .OnDelete(DeleteBehavior.Cascade);


            // ==========================================
            // ÍNDICES ÚNICOS
            // ==========================================

            modelBuilder.Entity<Usuario>()
                .HasIndex(u => u.Correo)
                .IsUnique();

            modelBuilder.Entity<Cliente>()
                .HasIndex(c => c.Correo)
                .IsUnique();

            modelBuilder.Entity<Categoria>()
                .HasIndex(c => c.Nombre)
                .IsUnique();

            modelBuilder.Entity<Pago>()
                .HasIndex(p => p.IdPedido)
                .IsUnique();
        }
    }
}