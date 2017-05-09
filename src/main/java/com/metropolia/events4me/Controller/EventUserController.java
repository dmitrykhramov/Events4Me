package com.metropolia.events4me.Controller;

import com.metropolia.events4me.Model.Event;
import com.metropolia.events4me.Model.User;
import com.metropolia.events4me.Service.EventUserService;
import com.metropolia.events4me.Service.LocationService;
import com.metropolia.events4me.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;


@Controller
public class EventUserController {

    private EventUserService eventUserService;
    private UserService userService;
    private LocationService locationService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEventUserService(EventUserService eventUserService) {
        this.eventUserService = eventUserService;
    }

    @Autowired
    public void setLocationService(LocationService locationService){
        this.locationService = locationService;
    }
    @RequestMapping("preferedevents")
    public @ResponseBody
    List<Event> getMatchedEventsForUser(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return eventUserService.matchEventsForUser(user);
    }

    /* This was done by Dima. I refactored it.
    @RequestMapping("join/event/{id}")
    public void joinEvent(Principal principal, @PathVariable Integer id) {
        User user = userService.findByUsername(principal.getName());
        eventUserService.joinEvent(user, id);
    }
    */

    @RequestMapping(value = "join/event/{id}", method = RequestMethod.POST)
    public void joinEventSubmit(Principal principal, @PathVariable Integer id) {
        User user = userService.findByUsername(principal.getName());
        eventUserService.joinEvent(user, id);
    }

    @RequestMapping(value = "/newEvent", method = RequestMethod.GET)
    public String newEventForm(Model model) {
        model.addAttribute("locations", locationService.listLocations());

        model.addAttribute("event",new Event());
        return "event/newEvent";
    }


    @RequestMapping(value = "/newEvent", method = RequestMethod.POST)
    public String newEventSubmit(@ModelAttribute Event event, @ModelAttribute String locationID, Principal principal, Model model) {
        User organizer = userService.findByUsername(principal.getName());
        // event.setLocation(locationService.listLocations().get(event.getLocationID()-1));
        model.addAttribute("result", eventUserService.createEvent(organizer, event));
        return "redirect:/event/show/" + event.getEventId();
    }
}
