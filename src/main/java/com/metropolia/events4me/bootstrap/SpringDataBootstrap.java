package com.metropolia.events4me.bootstrap;

import com.metropolia.events4me.Model.Event;
import com.metropolia.events4me.Model.Interest;
import com.metropolia.events4me.Model.User;
import com.metropolia.events4me.Model.security.Role;
import com.metropolia.events4me.Service.EventService;
import com.metropolia.events4me.Service.RoleService;
import com.metropolia.events4me.Service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class SpringDataBootstrap implements ApplicationListener<ContextRefreshedEvent> {


    private UserService userService;


    private EventService eventService;


    private RoleService roleService;

    @Autowired
    @Qualifier("UserServiceImpl")
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        loadEvents();
        loadRoles();
        loadUsers();
//        assignUserRole();
        assignAdminRole();

    }

    private void assignAdminRole() {
        List<Role> roles = roleService.listRoles();
        List<User> users = userService.listUsers();

        roles.forEach(role -> {
            if (role.getRole().equalsIgnoreCase("ADMIN")) {
                users.forEach(user -> {
                    if (user.getUsername().equals("dima")) {
                        user.addRole(role);
                        userService.saveOrUpdateUser(user);
                    }
                });
            }
        });

    }

    private void assignUserRole() {
        List<Role> roles = roleService.listRoles();
        List<User> users = userService.listUsers();

        roles.forEach(role -> {
            if (role.getRole().equalsIgnoreCase("USER")) {
                users.forEach(user -> {
                        user.addRole(role);
                        userService.saveOrUpdateUser(user);
                });
            }
        });
    }

    private void loadRoles() {
        Role role = new Role();
        role.setRole("USER");
        roleService.saveOrUpdateRole(role);

        Role adminRole = new Role();
        adminRole.setRole("ADMIN");
        roleService.saveOrUpdateRole(adminRole);
    }

    private void loadUsers() {
/*
        User dmitry = new User();
        dmitry.setUsername("dima");
        dmitry.setFirstName("dmitry");
        dmitry.setPassword("admin");
        dmitry.getInterests().add(Interest.BUSINESS);
        dmitry.getInterests().add(Interest.SPORT);
        dmitry.getInterests().add(Interest.DANCE);
        userService.saveOrUpdateUser(dmitry);

        User martin = new User();
        martin.setUsername("martin");
        martin.setFirstName("martin");
        martin.setPassword("user");
        martin.getInterests().add(Interest.PARTY);
        martin.getInterests().add(Interest.ART);

        martin.setFriend(dmitry);
        dmitry.setFriend(martin);
        dmitry.getFriends().add(martin);
        martin.getFriends().add(dmitry);
        userService.saveOrUpdateUser(martin);


        User niklas = new User();
        niklas.setUsername("nilas");
        niklas.setFirstName("niklas");
        niklas.setPassword("user");
        niklas.getInterests().add(Interest.BUSINESS);
        niklas.getInterests().add(Interest.SPORT);
        niklas.getInterests().add(Interest.DANCE);
        userService.saveOrUpdateUser(niklas);

        User user4 = new User();
      user4.setUsername("user4");
      user4.setFirstName("firstname4");
      user4.setPassword("user");
      user4.getInterests().add(Interest.BUSINESS);
      user4.getInterests().add(Interest.NATURE);
      userService.saveOrUpdateUser(user4);
*/

        User test5 = new User();
        test5.setUsername("test5");
        test5.setFirstName("firstname5");
        test5.setLastName("lastname5");
        test5.setEmail("test5@email.com");
        test5.setPassword("admin");
        test5.getInterests().add(Interest.BUSINESS);
        test5.getInterests().add(Interest.SPORT);
        test5.getInterests().add(Interest.DANCE);
        //TODO: find a way to remove such if condition. improve saveorupdate metohd
        userService.saveOrUpdateUser(test5);

        User retrieved = userService.findByUsername("test5");
        retrieved.setFirstName("another name");
        userService.saveOrUpdateUser(retrieved);
        /*
        if(!userService.checkUsernameExists("test5")){
            userService.saveOrUpdateUser(test5);
        }


        User test6 = new User();
        test6.setUsername("test6");
        test6.setFirstName("firstname6");
        test6.setLastName("lastname6");
        test6.setPassword("user");
        test6.setEmail("test6@email.com");
        test6.getInterests().add(Interest.PARTY);
        test6.getInterests().add(Interest.ART);

        if(!userService.checkUsernameExists("test6")){
            userService.saveOrUpdateUser(test6);
        }


        User test7 = new User();
        test7.setUsername("test7");
        test7.setFirstName("firstname7");
        test7.setLastName("lastname7");
        test7.setPassword("user");
        test7.setEmail("test7@email.com");
        test7.getInterests().add(Interest.BUSINESS);
        test7.getInterests().add(Interest.SPORT);
        test7.getInterests().add(Interest.DANCE);
        if(!userService.checkUsernameExists("test7")){
            userService.saveOrUpdateUser(test7);
        }

        User test8 = new User();
        test8.setUsername("test8");
        test8.setFirstName("firstname8");
        test8.setLastName("lastname8");
        test8.setPassword("user");
        test8.setEmail("test8@email.com");
        test8.getInterests().add(Interest.BUSINESS);
        test8.getInterests().add(Interest.NATURE);
        if(!userService.checkUsernameExists("test8")){
            userService.saveOrUpdateUser(test8);
        }
        */
    }

    private void loadEvents() {
        Event sportEvent = new Event();
        sportEvent.setName("Sport event");
        sportEvent.setDateTime(LocalDateTime.of(2017, 6, 2, 13, 0));
        sportEvent.setCategory(Interest.SPORT);
        eventService.saveOrUpdateEvent(sportEvent);

        Event partyEvent = new Event();
        partyEvent.setName("Party event");
        partyEvent.setDateTime(LocalDateTime.of(2017, 6, 10, 13, 0));
        partyEvent.setCategory(Interest.PARTY);
        eventService.saveOrUpdateEvent(partyEvent);

        Event businessEvent = new Event();
        businessEvent.setName("Business event");
        businessEvent.setDateTime(LocalDateTime.of(2017, 6, 2, 15, 0));
        businessEvent.setCategory(Interest.BUSINESS);
        eventService.saveOrUpdateEvent(businessEvent);

        Event businessEventPast = new Event();
        businessEventPast.setName("Business event past");
        businessEventPast.setDateTime(LocalDateTime.of(2017, 2, 2, 13, 0));
        businessEventPast.setCategory(Interest.BUSINESS);
        eventService.saveOrUpdateEvent(businessEventPast);

        Event sportEventPast = new Event();
        sportEventPast.setName("Sport event past");
        sportEventPast.setDateTime(LocalDateTime.of(2017, 2, 3, 13, 0));
        sportEventPast.setCategory(Interest.SPORT);
        eventService.saveOrUpdateEvent(sportEventPast);
    }
}
