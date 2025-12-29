package com.fay.uwsleeper.config;

import com.fay.uwsleeper.entity.NapSpot;
import com.fay.uwsleeper.repository.NapSpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



// seed data
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(NapSpotRepository repository) {
        return args -> {
            // SLC
            repository.save(new NapSpot("SLC", "Food court table", "table", 1, 5, 5, "don't try unless you love background noise", "Quite uncomfortable to sleep at these food court tables. It's usually very busy, loud and limited spots available during busy hours, but the upside is that it's conveniently near lots of good food places so you can grab munchies to energize during open hours. Very close to nearby main campus buildings like MC, STC, etc."));
            repository.save(new NapSpot("SLC", "3rd floor egg chairs (outside + lounge)", "chair", 5, 2, 3, "like you're sleeping in a movie","Both the egg chairs inside the 3rd floor lounge and in the 3rd floor public area have a beautiful ambience and view of MC/QNC outside the window. Very comfortable chairs, but usually busy and hard to get a spot, also very close to other people studying around. Typically not loud, perfect for napping before an exam/quiz if you're taking it in PAC or near SLC."));
            repository.save(new NapSpot("SLC", "Big round table on 2nd floor", "table", 3, 5, 4, "exude aura by sleeping at the congregation-size table","Usually taken. Kind of awkward if you're by yourself because others might come sit while you're sleeping, but perfect for group naps. Usually loud because of events going on in SLC basement, but can be nice white noise if you're into that for sleeping. Overall, good for quick recharges while studying. Comfort is pretty good for a table."));

            // E7/E5
            repository.save(new NapSpot("E7", "6th floor couch", "couch", 5, 2, 2, "can't beat the piano, fountain and eng student ambience", "Always open. Busy spot, but if you can snatch a couch, it's extremely comfortable and effective for power naps. Ambience is nice, might be people playing piano or talking, no windows, overall a good sleeping spot where people won't judge you. Can sometimes be dirty though."));
            repository.save(new NapSpot("E7", "5th floor quiet study desk", "table", 3, 1, 3, "niche, quiet, sunny and studious.","Niche locations, many possible spots on the 5th floor of E7/E5 to sleep. Sleeping at the desk is actually quite comfortable despite how it looks, and when it's sunny in the afternoon the rays of sunshine are perfect for putting you to sleep, especially when you're stressed. Lots of charging ports also. No noise, but possibly hard to find a spot."));
            repository.save(new NapSpot("E5", "SYDE lounge", "couch", 5, 1, 2, "closest thing to a bed you can find in e7","Reserved for SYDE, but very good enclosed spot if you can catch it at an empty time. Probably awkward sleeping there if people are studying, usually pretty busy among SYDE students. Good for overnight sleeping though if you have the door code, 3 couches all pretty comfortable."));
            repository.save(new NapSpot("E7", "2nd floor egg chairs", "chair", 5, 3, 3, "egg chairs on top","Sometimes people do dance practice there which can make it loud, but this spot has dim lighting and is decently comfortable. Pretty public area, recommended for longer periods sleep rather than quick convenient naps, as it may require moving the couch or setting up. Egg chairs more comfortable than the couches, can adjust."));
            repository.save(new NapSpot("E7", "2nd floor couch", "couch", 3, 3, 3, "dark spot near the e7 dancers","Sometimes people do dance practice there which can make it loud, but this spot has dim lighting and is decently comfortable. Pretty public area, recommended for longer periods sleep rather than quick convenient naps, as it may require moving the couch or setting up. Not as comfortable as the egg chairs."));

            // DC
            repository.save(new NapSpot("DC", "Main floor silent study room", "table", 1, 1, 3, "this + tims + nap = flow state recipe","VERY SILENT as the name suggests, absolutely no noise or talking. Lots of people nap in here, studious environment, but no windows, limited charging ports, and the lighting is pretty bright. However, quite uncomfortable because the chairs are too high for the tables. Lots of room on the table though."));
            repository.save(new NapSpot("DC", "Main floor quiet study couch", "couch", 4, 2, 4, "the type of couch you'll fall asleep on and never wake up","EXTREMELY comfortable couches, but awkward and very public location on the DC main floor. Despite it being in quiet study, some people talk but overall isn't too loud. Always busy, hard to snatch a spot but be prepared to not wake up if you fall asleep on one of these couches. Close to Tim Hortons and UW Plaza for food places"));

            // MKV
            repository.save(new NapSpot("MKV", "Table outside tutoring room", "table", 2, 1, 3, "be a mysterious sleeper in mkv.","Not the most comfortable and kind of awkward to sleep at these small tables. However the ambience is amazing, beautiful window views in the day and dim in the evening, perfect to fall asleep easy for a quick energy booster. Usually not loud but there are going to be lots of MKV residents walking by. Close to V1 and REV, far from main campus buildings."));

            // CMH
            repository.save(new NapSpot("CMH", "Great Hall", "table", 4, 4, 5, "gain aura by sleeping while all the eng kids are studying","CMH Great Hall is almost always super busy, but if you're able to snatch a table for studying, it's a nice place to take a quick power nap. Volume is usually not too high, except for peak exam times or evening hours. The chair and tables are pretty comfortable to sleep on though, and the studious environment with windows is very nice. Close to engineering buildings"));

            // V1
            repository.save(new NapSpot("V1", "Great Hall egg chairs", "chair", 5, 5, 5, "comfiness amidst the chaos","Pretty comfy chairs and it's really nice and warm in the afternoon if the sun is shining. However, limited chairs, very busy and social area so people you know will probably see you sleeping. Good for a deep, restful slumber if you're able to fall asleep though. Closer to main campus buildings than REV or MKV."));

            // REV
            repository.save(new NapSpot("REV", "Great Hall", "couch", 5, 3, 3, "sleep in the slums","Limited couches, but very comfortable to sleep in (especially for a deep slumber) but your friends will probably see and expose you if they're studying there. Not the best ambience, no windows and it's a gym-like large room. Usually not too loud. Pretty far away from main buildings on campus, not worth it unless you're in MKV or V1"));

            // MC
            repository.save(new NapSpot("MC", "3005 Mac Lab", "chair", 4, 2, 2, "niche, comfy cs hangout spot","Niche study spot, will probably only find CS/Math people in here. Chairs are extremely comfortable, tables not so much since there's computers taking up space, but nice for a quick recharge if you're in MC. A lot less busy than MC comfy, usually quiet but when there's friend groups in there it can get loud."));
        };
    }
}
