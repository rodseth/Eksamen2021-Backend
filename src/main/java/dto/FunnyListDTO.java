package dto;

import java.util.ArrayList;

public class FunnyListDTO {

    private ArrayList<FunnyDTO> memes;


    public FunnyListDTO() {
    }

    public ArrayList<FunnyDTO> getMemes() {
        return memes;
    }

    public void setMemes(ArrayList<FunnyDTO> memes) {
        this.memes = memes;
    }
}
