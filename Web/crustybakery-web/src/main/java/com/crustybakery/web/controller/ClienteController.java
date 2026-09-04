package com.crustybakery.web.controller;

import com.crustybakery.web.dto.ClienteDto;
import com.crustybakery.web.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new ClienteDto());
        return "clientes/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", clienteService.obtener(id));
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") ClienteDto cliente,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "clientes/formulario";
        }
        clienteService.guardar(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente guardado correctamente");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        clienteService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado");
        return "redirect:/clientes";
    }
}
