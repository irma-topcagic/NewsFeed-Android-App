package etf.ri.rma.newsfeedapp.data

import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    private val newsList: List<NewsItem> by lazy {
        listOf(
            NewsItem("1", "Naučnici razvijaju novu metodu za skladištenje obnovljive energije",
                "Otkriće bi moglo revolucionirati energetsku industriju.", null,
                "Nauka/tehnologija", false, "TechRadar", "2025-04-07"),
            NewsItem("2", "Spektakularan preokret u posljednjoj minuti donosi pobjedu domaćinu",
                "Utakmica je završila rezultatom 3:2 nakon dramatičnog gola u finišu.",
                null, "Sport", true, "Sport Klub", "2025-02-07"),
            NewsItem("3", "Domen Prevc obara svjetski rekord skokom od 254,5 metara u Planici",
                "Slovenački ski skakač Domen Prevc postavio je novi svjetski rekord skokom od 254,5 metara na takmičenju u Planici, " +
                        "nadmašivši prethodni rekord Stefana Krafta iz 2017. godine.",
                null, "Sport", true, "Reuters", "2025-03-30"),
            NewsItem("4", "Bosanskohercegovački startup razvija aplikaciju za automatsko prepoznavanje biljaka",
                "Aplikacija koristi AI za analizu fotografija u realnom vremenu.",
                null, "Nauka/tehnologija", true, "Netokracija", "2025-04-03"),
            NewsItem("5", "Nogometna reprezentacija BiH ostvarila drugu pobjedu u kvalifikacijama",
                "Bosna i Hercegovina pobijedila je Kipar rezultatom 2:1.", null, "Sport", true,
                "Al Jazeera Balkans", "2025-03-24"),
            NewsItem("6", "Izetbegović: Niko ne treba pregovarati s Dodikom",
                "Predsjednik SDA, Bakir Izetbegović, izjavio je da niko ne treba pregovarati s Miloradom Dodikom, " +
                        "naglašavajući da će on biti uhapšen, ali nije siguran kada i kako.",
                null, "Politika", true, "Vijesti.ba", "2025-04-07"),
            NewsItem("7", "Hamilton dobija podršku Ferrarija pred Veliku nagradu Bahreina",
                "Lewis Hamilton, sada vozač Ferrarija, traži preokret u sezoni na predstojećoj Velikoj nagradi Bahreina",
                null, "Sport", false, "talksport.com", "2025-02-10"),
            NewsItem("8","Nova politička strategija za narednu dekadu izaziva oprečne reakcije",
                "Vlada je objavila planove za sveobuhvatne reforme...",
                null, "Politika",false,"Dnevni avaz","2025-04-07"),
            NewsItem("9", "Naučnici razvili revolucionarnu tehnologiju koja može da promijeni svakodnevni život",
                "Tehnološki napredak", null, "Nauka/tehnologija", false, "Naučni članak", "2025-01-09"),
            NewsItem( "10", "Političke tenzije rastu nakon neusaglašenih stavova o budžetu",
                "Rasprava u parlamentu dodatno je produbila razlike među koalicionim partnerima.",
                null, "Politika", false, "Al Jazeera Balkans", "2025-03-05"),
            NewsItem( "11", "Hrvatska saznala protivnike u kvalifikacijama za SP 2026",
                "Hrvatska će igrati u grupi L zajedno s Češkom, Crnom Gorom, Farskim Otocima i Gibraltarom.",
                null, "Sport", false, "Gol.hr", "2025-03-21"),
            NewsItem( "12", "Bećirović uputio zahtjev za hitnu sjednicu Predsjedništva BiH",
                "Član Predsjedništva BiH, Denis Bećirović, zatražio je hitnu sjednicu Predsjedništva zbog aktuelne političke situacije u zemlji.",
                null, "Politika", false, "Slobodna Bosna", "2025-04-03"
            ),
            NewsItem( "13", "Amazon planira lansirati 3.000 satelita za globalni internet",
                "Amazon je najavio misiju 'Kuiper Atlas 1' za lansiranje satelita koji će pružati širokopojasni internet na globalnom nivou, konkurirajući Starlinku.",
                null, "Nauka/tehnologija", false, "Al Jazeera Balkans", "2025-04-03"),
            NewsItem(  "14",  "NASA i SpaceX šalju novu posadu na svemirsku stanicu",
                "NASA i SpaceX su lansirali zamjensku posadu za Međunarodnu svemirsku stanicu kako bi omogućili povratak astronauta koji su bili zaglavljeni na stanici.",
                null, "Nauka/tehnologija", true, "Glas Amerike", "2025-03-15"),
            NewsItem("15", "Paracetamol može izazvati promjene ličnosti", "Naučnici su otkrili da paracetamol može izazvati bizarne promjene ličnosti, što otvara nova pitanja o njegovom uticaju na ljudsko ponašanje.",
                null, "Nauka/tehnologija", true, "Nezavisne novine", "2025-04-05"),
            NewsItem( "16", "Favoriti za osvajanje Mastersa 2025: Scheffler i McIlroy u borbi na Augusti",
                "Masters turnir 2025. godine na Augusta Nationalu počinje 10. aprila, s Scottiejem Schefflerom i Roryjem McIlroyem kao glavnim favoritima za osvajanje zelene jakne.", null,
                "Sport", false, "talksport.com", "2025-03-07"),
            NewsItem( "17", "HDZ i SNSD blokirali Dom naroda: Dugo se nećemo sastati!",
                "Stranke HDZ i SNSD blokirale su rad Doma naroda, što je izazvalo političke tenzije i odgađanje daljih sjednica.",
                null, "Politika", false, "Vijesti.ba", "2025-02-07"),
            NewsItem("18", "Cristiano Ronaldo postigao 800. gol u profesionalnoj karijeri",
                "Cristiano Ronaldo postigao je svoj 800. gol u profesionalnoj karijeri, postavši jedan od najprolifičnijih napadača u povijesti nogometa.",
                null, "Sport", true, "Marca", "2025-04-06"),
            NewsItem("19", "EU usvojila nove sankcije prema Rusiji zbog vojne eskalacije u Ukrajini", "Europska unija" +
                    " usvojila je nove sankcije protiv Rusije, osudivši vojnu eskalaciju u Ukrajini, a naglasila je potrebu za daljnjim diplomatskim naporima.",
                null, "Politika", false, "Reuters", "2025-04-04"),
            NewsItem( "20", "Sudan: Politička kriza u zemlji se pogoršava, deseci poginulih u sukobima",
                "Politička kriza u Sudanu se pogoršava, a deseci ljudi su poginuli u nasilnim sukobima između vojnog i civilnog vodstva.",
                null, "Politika", false, "Al Jazeera", "2025-04-02"),
            NewsItem( "21", "Lionel Messi pomogao Interu Miami u osvojenju prve MLS titule",
                "Lionel Messi je svojim nastupom pomogao Interu Miami da osvoji prvu MLS titulu, ostvarivši izvanredne golove u finalu.",
                null, "Sport", true, "ESPN", "2025-04-03"),
            NewsItem( "22", "NASA lansira novu misiju za istraživanje Marsa", "NASA je uspješno lansirala novu misiju koja ima za cilj istraživanje mogućnosti ljudske kolonizacije Marsa, a prvi podaci očekuju se za nekoliko mjeseci.",
                null, "Svijet", false, "NASA", "2025-04-05"
            )
        )
    }
    fun getAllNews(): List<NewsItem> = newsList
}