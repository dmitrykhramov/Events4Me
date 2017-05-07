package com.metropolia.events4me.Service.ServiceImpl;


import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Freebusy;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.metropolia.events4me.Model.Event;
import com.metropolia.events4me.Model.Interest;
import com.metropolia.events4me.Model.User;
import com.metropolia.events4me.Service.EventService;
import com.metropolia.events4me.Service.EventUserService;
import com.metropolia.events4me.Service.UserService;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class EventUserServiceImpl implements EventUserService {

  private UserService userService;
  private EventService eventService;

  @Autowired
  @Qualifier("UserServiceImpl")
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Autowired
  public void setEventService(EventService eventService) {
    this.eventService = eventService;
  }

  // The method returns the list of recommended events
  // for the user according to his interests
  @Override
  public List<Event> matchEventsForUser(User user) {
    Set<Interest> interests = user.getInterests();
    List<Event> futureEvents = eventService.listFutureEvents();
    return futureEvents.stream()
        .filter(event -> interests.contains(event.getCategory()))
        .collect(Collectors.toList());
  }

  //TODO make integration test for this
  @Override
  public void joinEvent(User user, Integer eventId) {
    Event event = eventService.getEventById(eventId);
    user.enrolEvent(event);
    event.acceptAttendee(user);
    userService.saveOrUpdateUser(user);
    eventService.saveOrUpdateEvent(event);
  }

  @Override
  public String createEvent(User user, Event event) {
    if(timeSlotIsFree(event)){
      user.organizeNewEvent(event);
      //event.setOrganizer(user);
      if(!createEventOnCalendar(event)){
        return "There was an error. Couldn't create the event on Calendar.";
      }
      userService.saveOrUpdateUser(user);
      eventService.saveOrUpdateEvent(event);
      return "Event was created successfully";
    }
    return "Specified timeslot for tihs location is not free";
  }


  private boolean createEventOnCalendar(Event event) {
    String calendarID = event.getLocation().getCalendarID();
    com.google.api.services.calendar.model.Event googleEvent = new com.google.api.services.calendar.model.Event()
        .setSummary(event.getTitle())
        .setLocation(event.getLocation().getAddress())
        .setDescription(event.getDescription());

    DateTime startDate = new DateTime(event.getStartTime().toString() + ":00+03:00");
    EventDateTime start = new EventDateTime()
        .setDateTime(startDate)
        .setTimeZone("Europe/Helsinki");
    googleEvent.setStart(start);

    DateTime endDate = new DateTime(event.getEndTime().toString() + ":00+03:00");
    EventDateTime end = new EventDateTime()
        .setDateTime(endDate)
        .setTimeZone("Europe/Helsinki");
    googleEvent.setEnd(end);

    try {
      googleEvent = getCalendar().events().insert(calendarID, googleEvent).execute();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    System.out.printf("mmm Event created: %s\n", googleEvent.getHtmlLink());

    return true;
  }


  private boolean timeSlotIsFree(Event event) {
    String calendarID = event.getLocation().getCalendarID();
    FreeBusyRequest fbrq = new FreeBusyRequest();
    DateTime startDate = new DateTime(event.getStartTime().toString() + ":00+03:00");
    DateTime endTime = new DateTime(event.getEndTime().toString() + ":00+03:00");

    fbrq.setTimeMin(startDate);
    fbrq.setTimeMax(endTime);
    FreeBusyRequestItem fbrqItem = new FreeBusyRequestItem();
    fbrqItem.setId(calendarID);
    List<FreeBusyRequestItem> listOffbrqItems = new ArrayList<FreeBusyRequestItem>();
    listOffbrqItems.add(fbrqItem);

    fbrq.setItems(listOffbrqItems);

    Freebusy.Query fbq = null;
    FreeBusyResponse fbResponse = null;
    try {
      fbq = getCalendar().freebusy().query(fbrq);
      fbResponse = fbq.execute();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(fbResponse.toString());

    StringBuilder busyPeriods = new StringBuilder("");
    for (TimePeriod period : fbResponse.getCalendars().get(calendarID).getBusy()) {
      busyPeriods.append(period.toString());
    }

    if (busyPeriods.length() < 10){
      return true;
    } else {
      return false;
    }
  }

  //TODO: create singleton of this method
  private Calendar getCalendar() {
    JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    HttpTransport HTTP_TRANSPORT = null;
    //TODO: use logger
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    GoogleCredential credentials = null;
    //TODO: change the path to relative
    try {
      credentials = GoogleCredential
          .fromStream(new FileInputStream("src/main/resources/Events4Me.json"))
          .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
    } catch (IOException e) {
      e.printStackTrace();
    }

    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
        .setApplicationName("applicationName").build();
    return service;
  }



}
