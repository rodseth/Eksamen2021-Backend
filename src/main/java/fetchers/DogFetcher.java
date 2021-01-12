package fetchers;

import com.google.gson.Gson;
import dto.DogDTO;
import dto.FunnyDTO;
import dto.MemeDTO;
import dto.YesOrNoDTO;
import entities.Meme;
import utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DogFetcher {


    private static String dogURL = "https://dog.ceo/api/breeds/image/random";
    private static int numberOfTasks = 5;

    public static String fetchDog(ExecutorService threadpool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException {

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < numberOfTasks ; i++) {
            urls.add(dogURL);
        }

        ArrayList<Future<DogDTO>> futures = new ArrayList<>();

        for (String url : urls) {
        Callable<DogDTO> dogTask = new Callable<DogDTO>() {
            @Override
            public DogDTO call() throws Exception {
                String dog = HttpUtils.fetchData(url);
                DogDTO dogDTO = gson.fromJson(dog, DogDTO.class);
                return dogDTO;
            }};
            futures.add(threadpool.submit(dogTask));
        };

        List<DogDTO> result = new ArrayList<>();
        for (Future<DogDTO> dtos: futures) {
            result.add(dtos.get(2, TimeUnit.SECONDS));

        }

        List<MemeDTO> memeDTOS = new ArrayList<>();

        for (DogDTO dtos: result) {
            memeDTOS.add(new MemeDTO(dtos));
        }

        return gson.toJson(memeDTOS);
    }
}
