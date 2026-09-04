package com.crustybakery.web.controller;

import com.crustybakery.web.dto.PagoDto;
import com.crustybakery.web.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("ventas", ventaService.listarVentas());
        return "ventas/lista";
    }

    @GetMapping("/{pedidoId}/pagos")
    public String pagos(@PathVariable Integer pedidoId, Model model) {
        model.addAttribute("pedidoId", pedidoId);
        model.addAttribute("pagos", ventaService.listarPagos(pedidoId));
        PagoDto nuevoPago = new PagoDto();
        nuevoPago.setPedidoId(pedidoId);
        model.addAttribute("nuevoPago", nuevoPago);
        return "ventas/pagos";
    }

    @PostMapping("/{pedidoId}/pagos/registrar")
    public String registrarPago(@PathVariable Integer pedidoId,
                                 @ModelAttribute("nuevoPago") PagoDto pago,
                                 RedirectAttributes redirectAttributes) {
        pago.setPedidoId(pedidoId);
        ventaService.registrarPago(pago);
        redirectAttributes.addFlashAttribute("mensaje", "Pago registrado correctamente");
        return "redirect:/ventas/" + pedidoId + "/pagos";
    }
}
