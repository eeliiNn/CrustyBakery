package com.crustybakery.web.controller;

import com.crustybakery.web.dto.DetallePedidoDto;
import com.crustybakery.web.dto.PedidoDto;
import com.crustybakery.web.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("pedidos", pedidoService.listar());
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pedido", new PedidoDto());
        model.addAttribute("clientes", pedidoService.listarClientes());
        return "pedidos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("pedido") PedidoDto pedido, RedirectAttributes redirectAttributes) {
        PedidoDto creado = pedidoService.crear(pedido);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido creado correctamente");
        return "redirect:/pedidos/" + creado.getId();
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        model.addAttribute("pedido", pedidoService.obtener(id));
        model.addAttribute("productos", pedidoService.listarProductos());
        model.addAttribute("nuevoDetalle", new DetallePedidoDto());
        return "pedidos/detalle";
    }

    @PostMapping("/{id}/detalles/agregar")
    public String agregarDetalle(@PathVariable Integer id,
                                  @ModelAttribute("nuevoDetalle") DetallePedidoDto detalle,
                                  RedirectAttributes redirectAttributes) {
        pedidoService.agregarDetalle(id, detalle);
        redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al pedido");
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/detalles/{detalleId}/eliminar")
    public String eliminarDetalle(@PathVariable Integer id, @PathVariable Integer detalleId,
                                   RedirectAttributes redirectAttributes) {
        pedidoService.eliminarDetalle(id, detalleId);
        redirectAttributes.addFlashAttribute("mensaje", "Producto quitado del pedido");
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, @RequestParam String estado,
                                 RedirectAttributes redirectAttributes) {
        pedidoService.cambiarEstado(id, estado);
        redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado a " + estado);
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        pedidoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido eliminado");
        return "redirect:/pedidos";
    }
}
