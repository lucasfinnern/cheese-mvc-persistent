package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("title", "Menu List");
        model.addAttribute("menuList", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processMenuForm(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model) {

        Menu passedMenu = menuDao.findOne(menuId);
        model.addAttribute("menu", passedMenu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable int menuId, Model model) {
        Menu passedMenu = menuDao.findOne(menuId);
        Iterable<Cheese> cheeseDB = cheeseDao.findAll();
        AddMenuItemForm newMenuItemForm = new AddMenuItemForm(passedMenu, cheeseDB);

        model.addAttribute("title", "Add item to menu: " + passedMenu.getName());
        model.addAttribute("form", newMenuItemForm);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuID}", method = RequestMethod.POST)
    public String processAddItemForm(@ModelAttribute @Valid AddMenuItemForm aMenuItemForm, Errors errors
            , @RequestParam int cheeseId, int menuId, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title",
                    "Add item to menu: " + aMenuItemForm.getMenu().getName());
            model.addAttribute("form", aMenuItemForm);

            return "menu/add-item";
        }

        Cheese addedCheese = cheeseDao.findOne(cheeseId);
        Menu changedMenu = menuDao.findOne(menuId);
        changedMenu.addItem(addedCheese);
        menuDao.save(changedMenu);

        return "redirect:/menu/view/" + changedMenu.getId();
    }



}
