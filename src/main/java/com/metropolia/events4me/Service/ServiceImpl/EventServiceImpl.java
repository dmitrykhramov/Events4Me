package com.metropolia.events4me.Service.ServiceImpl;

import com.metropolia.events4me.DAO.EventDAO;
import com.metropolia.events4me.Model.Event;
import com.metropolia.events4me.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class EventServiceImpl implements EventService {

    private EventDAO eventDAO;

    @Autowired
    public void setEventDAO(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    private Sort sortByDateTimeAsc() {
        return new Sort(Sort.Direction.ASC, "endTime");
    }

    @Override
    public List<Event> listAllEvents() {
        return eventDAO.findAll(sortByDateTimeAsc());
    }

    @Override
    public List<Event> listPastEvents() {
        List<Event> allEvents = listAllEvents();
        LocalDate today = LocalDate.now();
        List<Event> pastEvents = new ArrayList<>();
        allEvents.forEach(event -> {
            if (event.getEndTime().toLocalDate().isBefore(today)) {
                pastEvents.add(event);
            }
        });
        return pastEvents;
    }

    @Override
    public List<Event> listFutureEvents() {
        List<Event> allEvents = listAllEvents();
        LocalDate today = LocalDate.now();
        List<Event> futureEvents = new ArrayList<>();
        allEvents.forEach(event -> {
            if (event.getEndTime().toLocalDate().isAfter(today)) {
                futureEvents.add(event);
            }
        });
        return futureEvents;
    }

    @Override
    public Event findById(int id) {
        return eventDAO.findOne(id);
    }

    @Override
    public Event saveOrUpdateEvent(Event event) {
        return eventDAO.save(event);
    }

    @Override
    public Event getEventById(Integer id) {
        return eventDAO.findOne(id);
    }

    @Override
    public void delete(Integer id) {
        eventDAO.delete(id);
    }

}
