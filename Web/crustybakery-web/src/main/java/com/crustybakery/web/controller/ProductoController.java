package com.crustybakery.web.controller;

import com.crustybakery.web.dto.ProductoDto;
import com.crustybakery.web.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String lista(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new ProductoDto());
        model.addAttribute("categorias", productoService.listarCategorias());
        return "productos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", productoService.obtener(id));
        model.addAttribute("categorias", productoService.listarCategorias());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") ProductoDto producto,
                           BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", productoService.listarCategorias());
            return "productos/formulario";
        }
        productoService.guardar(producto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto guardado correctamente");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado");
        return "redirect:/productos";
    }
}
