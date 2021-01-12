package fetchers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.CatDTO;
import dto.FunnyDTO;
import dto.MemeDTO;
import entities.Meme;
import utils.HttpUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CatFetcher {

    private static String catURL = "https://api.thecatapi.com/v1/images/search";

    private static int numberOfTasks = 5;

    public static String fetchCat(ExecutorService threadpool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException {


        ArrayList<String> urls = new ArrayList<>();
        ArrayList<Future<CatDTO>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfTasks; i++) {
            urls.add(catURL);
        }
        for (String url: urls) {

        Callable<CatDTO> catTask = new Callable<CatDTO>() {
            @Override
            public CatDTO call() throws Exception {
                String cat = HttpUtils.fetchData(url);
                Type listType = new TypeToken<List<CatDTO>>() {}.getType();

                List<CatDTO> catDTO = gson.fromJson(cat,listType);

                return catDTO.get(0);
            }};
          futures.add(threadpool.submit(catTask));
        }

        List<CatDTO> result = new ArrayList<>();
        for (Future<CatDTO> fut : futures){
            result.add(fut.get(2, TimeUnit.SECONDS));
        }

        List<MemeDTO> memeDTOlist = new ArrayList<>();
        for (CatDTO dtos: result) {
            memeDTOlist.add(new MemeDTO(dtos));

        }

        return gson.toJson(memeDTOlist);
    }
}
