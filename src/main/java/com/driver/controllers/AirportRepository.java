package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AirportRepository {
    Map<String,Airport> airportMap=new HashMap<>();
    Map<Integer, Flight>flightMap=new HashMap<>();
    HashMap<Integer, List<Integer>> ticketHashMap = new HashMap<>();
    Map<Integer,Passenger>passengerMap=new HashMap<>();

    public void addAirport(Airport airport){

        airportMap.put(airport.getAirportName(), airport);
    }

    public String getLargestAirportName() {
        int maxTerminal=0;
        for(Airport airport:airportMap.values()){
            if(airport.getNoOfTerminals()>=maxTerminal){
                maxTerminal=airport.getNoOfTerminals();
            }
        }
        List<String>list=new ArrayList<>();
        for(Airport airport:airportMap.values()){
            if(airport.getNoOfTerminals()==maxTerminal){
                list.add(airport.getAirportName());
            }
        }
        Collections.sort(list);
        return list.get(0);
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double Duration=Double.MAX_VALUE;
        boolean isDirectFligt=false;
        for(int flightId:flightMap.keySet()){
            Flight flight=flightMap.get(flightId);
            if(flight.getFromCity()==fromCity&&flight.getToCity()==toCity){
                Duration=Double.min(Duration,flight.getDuration());
                isDirectFligt=true;
            }
        }
        if(isDirectFligt==false) Duration=-1;
        return Duration;
    }

    public String bookTicket(Integer flightId, Integer passengerId) {

        if(ticketHashMap.containsKey(flightId)){
            List<Integer> psgList=ticketHashMap.get(flightId);
            Flight flight=flightMap.get(flightId);
            if(flight.getMaxCapacity()==psgList.size())
                return "FAILURE";
            if(psgList.contains(passengerId))
                return "FAILURE";

            psgList.add(passengerId);
            ticketHashMap.put(flightId,psgList);
            return  "SUCCESS";
        }else{
            List<Integer>newPsgList=new ArrayList<>();
            newPsgList.add(passengerId);
            ticketHashMap.put(flightId,newPsgList);
            return  "SUCCESS";
        }

    }

    public String cancelATicket(Integer flightId, Integer passengerId) {

        if(ticketHashMap.containsKey(flightId)){
            boolean removed=false;
            List<Integer>psgList=ticketHashMap.get(flightId);
            if(psgList==null)return"FAILURE";
            if(psgList.contains(passengerId)){
                psgList.remove(passengerId);
                removed=true;
            }
            if(removed){
                ticketHashMap.put(flightId,psgList);
                return "SUCCESS";
            }else return "FAILURE";

        }
        return "FAILURE";
    }

    public String addFlight(Flight flight) {
        flightMap.put(flight.getFlightId(), flight);
        return "SUCCESS";
    }

    public String getAirportNameFlightId(Integer flightId) {
//        if(flightMap.containsKey(flightId)){
//            Flight flight=flightMap.get(flightId);
//            for(Airport airport:airportMap.values()){
//                if(airport.getCity().equals(flight.getFromCity())){
//                    return airport.getAirportName();
//                }
//            }
//        }
//        return null;

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

    public String addPassenger(Passenger passenger) {

        passengerMap.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }

    public int calculateFlightFare(Integer flightId) {
        int numOfPassenger=ticketHashMap.get(flightId).size();
        return 3000+(numOfPassenger*50);

    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int ans=0;
        if(airportMap.containsKey(airportName)){
            City city=airportMap.get(airportName).getCity();
            for(Integer flightId:ticketHashMap.keySet()){
                Flight flight =flightMap.get(flightId);
                if(flight.getFlightDate().equals(date)&&(flight.getFromCity().equals(city)||flight.getToCity().equals(city))){
                    ans+=ticketHashMap.get(flightId).size();
                }
            }
        }
        return ans;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int revenue=0;
        if(ticketHashMap.containsKey(flightId)){
            int size=ticketHashMap.get(flightId).size();
            for(int i=0;i<size;i++){
                revenue+=3000+(i*50);
            }
        }
        return revenue;
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int ans=0;
        for(List<Integer>psgList:ticketHashMap.values()){
            for(Integer id:psgList){

                if(id==passengerId){
                    ans++;
                }
            }
        }
        return ans;

    }
}
