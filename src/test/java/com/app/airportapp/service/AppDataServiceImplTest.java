package com.app.airportapp.service;

import com.app.airportapp.entity.AirPortData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class AppDataServiceImplTest {
    AppDataServiceImpl appDataService;
    @Before
    public void init(){
        appDataService= new AppDataServiceImpl();
    }
    @Test
    public void testLoadDataReturnData(){
        appDataService.load();
        LocalDate date = LocalDate.of(2021, 1, 8);
        List<AirPortData> lst = appDataService.getData(date);
        Assert.assertNotNull(lst);
        Assert.assertEquals(7,lst.size());

    }
    @Test
    public void testLoadReturnNull(){
        appDataService.load();
        LocalDate date = LocalDate.of(2022, 1, 8);
        List<AirPortData> lst = appDataService.getData(date);
        Assert.assertNull(lst);
    }


}