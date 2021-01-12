package fetchers;

import com.google.gson.Gson;
import dto.FunnyDTO;
import dto.MemeDTO;
import dto.YesOrNoDTO;
import entities.Meme;
import utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class YesOrNoFetcher {


    private static String yesOrNoURL = "https://yesno.wtf/api/";
    private static int numberOfTasks = 5;

    public static String fetchYesOrNo(ExecutorService threadpool, Gson gson) throws InterruptedException, ExecutionException, TimeoutException {

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 0; i < numberOfTasks ; i++) {
            urls.add(yesOrNoURL);
        }

        ArrayList<Future<YesOrNoDTO>> futures = new ArrayList<>();

        for (String url : urls) {
        Callable<YesOrNoDTO> yesOrNoTask = new Callable<YesOrNoDTO>() {
            @Override
            public YesOrNoDTO call() throws Exception {
                String yesOrNo = HttpUtils.fetchData(url);
                YesOrNoDTO yesOrNoDTO = gson.fromJson(yesOrNo, YesOrNoDTO.class);
                return yesOrNoDTO;
            }};
            futures.add(threadpool.submit(yesOrNoTask));
        };

        List<YesOrNoDTO> result = new ArrayList<>();
        for (Future<YesOrNoDTO> dtos: futures) {
            result.add(dtos.get(2, TimeUnit.SECONDS));

        }

        List<MemeDTO> memeDTOS = new ArrayList<>();

        for (YesOrNoDTO dtos: result) {
            memeDTOS.add(new MemeDTO(dtos));
        }

        return gson.toJson(memeDTOS);
    }
}
