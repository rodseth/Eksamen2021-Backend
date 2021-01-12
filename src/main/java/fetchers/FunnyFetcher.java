package fetchers;

import com.google.gson.Gson;
import dto.FunnyDTO;
import dto.FunnyListDTO;
import dto.MemeDTO;
import utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FunnyFetcher {

    private static String funnyURL = "https://meme-api.herokuapp.com/gimme/5";


    public static String fetchFunny(ExecutorService threadpool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException {



            Callable <FunnyListDTO> funnyTask = new Callable<FunnyListDTO>() {
                @Override
                public FunnyListDTO call() throws Exception {
                    String funny = HttpUtils.fetchData(funnyURL);
                    FunnyListDTO funnyDTOS = gson.fromJson(funny, FunnyListDTO.class);
                    return funnyDTOS;
                }
            };

       Future<FunnyListDTO> futureFunny = threadpool.submit(funnyTask);

       FunnyListDTO funnyDTOS = futureFunny.get(5, TimeUnit.SECONDS);

       List<MemeDTO> memeDTOS = new ArrayList<>();

        for (FunnyDTO dto: funnyDTOS.getMemes()) {
            memeDTOS.add(new MemeDTO(dto));
        }

        return gson.toJson(memeDTOS);
    }
}