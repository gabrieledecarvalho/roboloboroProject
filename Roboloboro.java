/**
 * Roboloboro - a robot by gabrieledecarvalho and fenamattos
 */

package roboloboropack;
import robocode.*;
// importando biblioteca de cores:
import java.awt.Color;
// importando biblioteca do robo avançado:
import robocode.AdvancedRobot;
// importanto util para utilizar angulos relativos/absolutos - para ser utilizado no onScannedRobotEvent:
import static robocode.util.Utils.normalRelativeAngleDegrees;
import robocode.util.*;

public class Roboloboro extends AdvancedRobot {
	 
	public void run() {		
		// MODIFICANDO AS CORES DO ROBO PARA QUE CADA PEÇA TENHA UMA COR DIFERENTE:
		setColors(new Color(150, 123, 182), new Color(153, 0, 0),new Color(255, 229, 180));
		
		// AJUSTANDO O ROBÔ AVANÇADO, ARMA E RADAR SE MOVEM LIVREMENTE:
		setAdjustGunForRobotTurn(true); //arma não se mexe com o corpo
		setAdjustRadarForGunTurn(true); //radar nao se mexe com a arma
		setAdjustRadarForRobotTurn(true); //radar não se mexe com o robo
		
		// Loop de movimentação do Roboloboro:
		while(true) {
			//DEFININDO ALGUMAS VARIÁVEIS EM RELAÇÃO AO CAMPO DE BATALHA:
			double wBattleField = getBattleFieldHeight(); //profundidade do campo de batalha
			double hBattleField = getBattleFieldWidth(); //altura do campo de batalha
			double diagonalBattleField = (Math.sqrt((Math.pow(wBattleField,2) + Math.pow(hBattleField,2))))/2;			
			setTurnRight(45);
			setAhead(diagonalBattleField/2);
			setTurnGunRight(360);
			execute(); //executa as 4 ações acima junto 
			turnRadarRight(360);
		}
	}
	
	// EVENTO: scanner detectou um robo:
	public void onScannedRobot(ScannedRobotEvent e) {
		// IDEIA: prever a próxima posição inimiga antes de atirar.
		
		// captando informações inimigas, em radianos pois a função Math.sin e Math.cos só funcionam em radianos:
		double direcaoRobo = getHeadingRadians(); //direcao no campo a qual meu robo está indo
		double eAngulo = e.getBearingRadians(); //posicao (angulo) a qual o inimigo está
		
		double anguloRelativo = direcaoRobo + eAngulo; //angulo em relação ao roboloboro
		double eDistancia = e.getDistance(); //distancia entre o ponto central do inimigo e o ponto central do roboloboro
		
		double eX = getX() + Math.sin(anguloRelativo) * eDistancia; //posicao x do robo inimigo
		double eY = getX() + Math.cos(anguloRelativo) * eDistancia; //posicao y do robo inimigo
		
		// PREVISOR - captando variáveis necessárias:
		double eVelocidade = e.getVelocity(); //velocidade do inimigo
		double eDirecao = e.getHeadingRadians();
		
		// PREVISOR - posicao lida do inimigo + o quanto ele vai andar baseado na velocidade que ele tem
		double ePrevisaoX = eX + Math.sin(eDirecao) * eVelocidade; //previsao de onde o inimigo estará (ponto x)
		double ePrevisaoY = eY + Math.cos(eDirecao) * eVelocidade;//previsao de onde o inimigo estará (ponto y)
		
		//PREVISOR - angulo absoluto do robo inimigo
		//utilizando a util de transformar o angulo em absoluto baseado na posicao original e ajustando com a previsao
		double anguloAbs = Utils.normalAbsoluteAngle(
			Math.atan2( // atan retorna um angulo
				ePrevisaoX - getX(), ePrevisaoY - getY() 
			)
		);
		
		double anguloRadar = getRadarHeadingRadians(); // angulo radar roboloboro
		setTurnRightRadians(eAngulo / 2* - 1 - anguloRadar); 
		setTurnRadarRightRadians(Utils.normalRelativeAngle(eAngulo - anguloRadar));
		setTurnGunRightRadians(Utils.normalRelativeAngle(anguloAbs - anguloRadar));
					
		double potenciaDoTiro = Math.min(2.0, getEnergy());//para que a potencia do tiro nunca seja maior que a energia restante
		fire(potenciaDoTiro);
	}
	
/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		double energia = getEnergy();
		double eDirecao = e.getBearing(); //direção que a bala chegou
		if(energia < 80){
			setTurnRight(-eDirecao); //vai para o lado oposto
			setAhead(100);
			execute();
		}else{
			turnGunRight(360);
		}
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		double wBattleField = getBattleFieldHeight(); //profundidade do campo de batalha
		double hBattleField = getBattleFieldWidth(); //altura do campo de batalha
		double diagonalBattleField = (Math.sqrt((Math.pow(wBattleField,2)+Math.pow(hBattleField,2))))/2;
		double eDirecao = e.getBearing(); //direção da parede
   	 	turnRight(-eDirecao); //vai para o lado oposto
	    ahead(diagonalBattleField/2);
	}

	public void onWin(WinEvent e) {
		turnRight(720);
		turnLeft(720);
	}
}
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html