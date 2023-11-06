import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

class GameRunnerTest {

    @Test
    public void test_getTheAxeOfTheMouvement(){
        assertThat(GameRunner.getTheAxeOfTheMovement("S")).isEqualTo("X") ;
        assertThat(GameRunner.getTheAxeOfTheMovement("N")).isEqualTo("X") ;
        assertThat(GameRunner.getTheAxeOfTheMovement("O")).isEqualTo("Y") ;
        assertThat(GameRunner.getTheAxeOfTheMovement("E")).isEqualTo("Y") ;
    }

    @Test
    public void test_getTheIndexOfTheDirection(){
        assertThat(GameRunner.getTheIndexOfTheDirection("S")).isEqualTo(3) ;
        assertThat(GameRunner.getTheIndexOfTheDirection("N")).isEqualTo(0) ;
        assertThat(GameRunner.getTheIndexOfTheDirection("O")).isEqualTo(1) ;
        assertThat(GameRunner.getTheIndexOfTheDirection("E")).isEqualTo(2) ;
        assertThat(GameRunner.getTheIndexOfTheDirection("F")).isEqualTo(-1) ;
    }

    @Test
    public void test_getAventurierNextStep(){
        assertThat( GameRunner.getAdventurerNextStep("X","S",new Tuple2<>(1,1),5,3)).isEqualTo(new GameRunner.AdventurerNextStepPosition(2,1));
        assertThat( GameRunner.getAdventurerNextStep("X","N",new Tuple2<>(1,1),5,3)).isEqualTo(new GameRunner.AdventurerNextStepPosition(0,1));
        assertThat( GameRunner.getAdventurerNextStep("Y","O",new Tuple2<>(1,1),5,3)).isEqualTo(new GameRunner.AdventurerNextStepPosition(1,0));
        assertThat( GameRunner.getAdventurerNextStep("Y","E",new Tuple2<>(1,1),5,3)).isEqualTo(new GameRunner.AdventurerNextStepPosition(1,2));
    }

}