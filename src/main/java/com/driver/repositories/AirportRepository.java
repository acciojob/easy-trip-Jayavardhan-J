package com.driver.repositories;
import java.util.*;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;


@Repository
public class AirportRepository {
    Map<Integer, Passenger> passengerMap=new HashMap<>();
    Map<String,Airport> airportMap = new HashMap<>();
    HashMap<Integer,List<Integer>> ticketMap = new HashMap<>();
    Map<Integer,Flight> flightMap = new HashMap<>();
    public AirportRepository() {
    }

    public String addAirport(Airport airport) {
        airportMap.put(airport.getAirportName(),airport);
        return "SUCCESS";
    }

    public String getLargestAirportName() {

        int maxTerminal=0;
        for(Airport airport:airportMap.values()){
            if(airport.getNoOfTerminals()>=maxTerminal){
                maxTerminal=airport.getNoOfTerminals();
            }
        }
        List<String> list =  new ArrayList<>();
        for(Airport airport:airportMap.values()){
            if(airport.getNoOfTerminals()==maxTerminal)list.add(airport.getAirportName());
        }
        Collections.sort(list);
        return list.get(0);
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double duration=Double.MAX_VALUE;
        boolean isDirect=false;
        for(int flightId :flightMap.keySet()){
            Flight flight=flightMap.get(flightId);
            if(flight.getFromCity()==fromCity && flight.getToCity()==toCity){
                duration=Double.min(duration,flight.getDuration());
                isDirect=true;
            }
        }
        if(!isDirect)return -1;
        return duration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int ans=0;
        if(airportMap.containsKey(airportName)){
            City city=airportMap.get(airportName).getCity();
            for(Integer flightId : ticketMap.keySet()){
                Flight flight=flightMap.get(flightId);
                if(flight.getFlightDate().equals(date) && (flight.getFromCity().equals(city) || flight.getToCity().equals(city))){
                    ans+=ticketMap.get(flightId).size();
                }
            }
        }
        return ans;
    }

    public int calculateFlightFare(Integer flightId) {
        return 3000+ticketMap.get(flightId).size();
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        if(ticketMap.containsKey(flightId)){
            List<Integer> pList=ticketMap.get(flightId);
            Flight flight=flightMap.get(flightId);
            if(flight.getMaxCapacity()==pList.size() || pList.contains(passengerId))return "FAILURE";
            pList.add(passengerId);
            ticketMap.put(flightId,pList);
            return "SUCCESS";
        }
        else{
            List<Integer> newPList = new ArrayList<>();
            newPList.add(passengerId);
            ticketMap.put(flightId,newPList);
            return "SUCCESS";
        }
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        if(ticketMap.containsKey(flightId)){
            boolean removed=false;
            List<Integer>pList =ticketMap.get(flightId);
            if(pList==null)return "FAILURE";
            if(pList.contains(passengerId)){
                pList.remove(passengerId);
                removed=true;
            }
            if(removed){
                ticketMap.put(flightId,pList);
                return "SUCCESS";
            }

        }
        return "FAILURE";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int ans=0;
        for(List<Integer>psgList:ticketMap.values()){
            for(Integer id:psgList){

                if(id==passengerId){
                    ans++;
                }
            }
        }
        return ans;
    }

    public String addFlight(Flight flight) {
        flightMap.put(flight.getFlightId(), flight);
        return "SUCCESS";
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        for (Flight flight : flightMap.values()) {
            if (flight.getFlightId() == flightId) {
                City city = flight.getFromCity();
                for (Airport airport : airportMap.values()) {
                    if (airport.getCity().equals(city))
                        return airport.getAirportName();
                }
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int revenue=0;
        if(ticketMap.containsKey(flightId)){
            int size=ticketMap.get(flightId).size();
            for(int i=0;i<size;i++){
                revenue+=3000+(i*50);
            }
        }
        return revenue;
    }

    public String addPassenger(Passenger passenger) {
        passengerMap.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }
}
