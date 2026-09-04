package com.crustybakery.web.controller;

import com.crustybakery.web.dto.CategoriaDto;
import com.crustybakery.web.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("categorias", categoriaService.listar());
        return "categorias/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("categoria", new CategoriaDto());
        return "categorias/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("categoria", categoriaService.obtener(id));
        return "categorias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") CategoriaDto categoria,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categorias/formulario";
        }
        categoriaService.guardar(categoria);
        redirectAttributes.addFlashAttribute("mensaje", "Categoria guardada correctamente");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        categoriaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Categoria eliminada");
        return "redirect:/categorias";
    }
}
