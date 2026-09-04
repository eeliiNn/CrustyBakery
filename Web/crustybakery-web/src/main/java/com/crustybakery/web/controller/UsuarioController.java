package com.crustybakery.web.controller;

import com.crustybakery.web.dto.UsuarioCreateDto;
import com.crustybakery.web.dto.UsuarioUpdateDto;
import com.crustybakery.web.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new UsuarioCreateDto());
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") UsuarioCreateDto usuario,
                           RedirectAttributes redirectAttributes) {
        usuarioService.crear(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("usuario", usuarioService.obtener(id));
        model.addAttribute("usuarioUpdate", new UsuarioUpdateDto());
        return "usuarios/formulario-editar";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Integer id,
                              @ModelAttribute("usuarioUpdate") UsuarioUpdateDto usuario,
                              RedirectAttributes redirectAttributes) {
        usuarioService.actualizar(id, usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        usuarioService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado");
        return "redirect:/usuarios";
    }
}
