package kopo.poly.controller;

import kopo.poly.dto.WasteGuide;
import kopo.poly.service.WasteCrawlerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WasteController {

    private final WasteCrawlerService svc;
    private WasteGuide cache;

    public WasteController(WasteCrawlerService svc) { this.svc = svc; }

    @GetMapping("/info/guide.json")
    @ResponseBody
    public WasteGuide api() throws Exception {
        if (cache == null) cache = svc.fetch();
        return cache;
    }

    @GetMapping("/info/guide/refresh")
    @ResponseBody
    public String refresh() throws Exception {
        cache = svc.fetch();
        return "ok";
    }

    @GetMapping("/info/guide")
    public String view(Model model) throws Exception {
        if (cache == null) cache = svc.fetch();
        model.addAttribute("g", cache);
        return "info/guide"; // templates/info/guide.html
    }
}
