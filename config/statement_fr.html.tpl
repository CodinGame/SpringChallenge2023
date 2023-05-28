<!-- LEAGUES level1 level2 level3 level4 -->
<div id="statement_back" class="statement_back" style="display: none"></div>
<div class="statement-body">
	<!-- BEGIN level1 -->
	<!-- LEAGUE ALERT -->
	<div style="color: #7cc576;
	background-color: rgba(124, 197, 118,.1);
	padding: 20px;
	margin-right: 15px;
	margin-left: 15px;
	margin-bottom: 10px;
	text-align: left;">
		<div style="text-align: center; margin-bottom: 6px">
			<img src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" />
		</div>
		<p style="text-align: center; font-weight: 700; margin-bottom: 6px;">
			Ce challenge est basé sur un système de <b>leagues</b>.
		</p>
		<div class="statement-league-alert-content">
			Pour ce challenge, plusieurs ligues pour le même jeu seront disponibles. Quand vous aurez prouvé votre valeur
			contre le premier Boss, vous accéderez à la ligue supérieure et débloquerez de nouveaux adversaires.
			<br>
			<br>
		</div>

	</div>
	<!-- END -->

	<!-- GOAL -->
	<div class="statement-section statement-goal">
		<h2 style="font-size: 20px;">
			<span class="icon icon-goal">&nbsp;</span>
			<span>Goal</span>
		</h2>
		Terminer la partie avec plus de cristaux que votre adversaire.
		<center style="margin: 20px">
			<img src="/servlet/fileservlet?id=104654740443345"
				style="height: 113px; margin-bottom: 5px">
			<div>
				<em>Cristaux</em>
			</div>
		</center>
		<p>Le jeu se déroule dans un <b>laboratoire</b>, dans lequel 2 scientifiques en charge de <b>fourmis robots</b>
			s'affrontent pour trouver le moyen le plus efficace pour récupérer des cristaux.</p>
		Cependant, les fourmis <b>ne peuvent pas être contrôlées directement</b>. Elles répondent uniquement à la présence
		de <b>balises</b>.

	</div>



	<!-- RULES -->
	<div class="statement-section statement-rules">
		<h2 style="font-size: 20px;">
			<span class="icon icon-rules">&nbsp;</span>
			<span>Règles</span>
		</h2>
		<div class="statement-rules-content">
			<div style="margin-bottom: 10px">
				Le jeu se joue au tour par tour. A chaque tour, chaque joueur réalise autant d'actions qu'il le souhaite de
				manière simultanée.

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					La carte</h3>

				<p>Pour chaque partie, la carte est <b>générée aléatoirement</b> et est composée de <b>cellules hexagonales</b>.
				</p>
				Chaque cellule a un <b>indice</b> et jusqu'à 6 voisines. Chaque direction est numérotée de <const>0</const> à
				<const>5</const>.

				<center style="margin: 20px">
					<img src="/servlet/fileservlet?id=104672349644259"
						style="height: 226px; margin-bottom: 5px">
					<div>
						<em>Directions</em>
					</div>
				</center>
				<br>


				Chaque cellule a un <b>type</b>, qui indique ce qu'elle contient:
				<ul>
					<li>
						<const>0</const> si elle ne contient aucune ressource.
					</li>
					<!-- BEGIN level1 -->
					<li><em>
							<const>1</const> apparaitra dans les prochaines ligues et peut être ignoré pour le moment.
						</em></li>
					<!-- END -->

					<!-- BEGIN level2 level3 level4 -->
					<!-- BEGIN level2 -->
					<li style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
						<!-- END -->
						<!-- BEGIN level3 level4 -->
					<li>
						<!-- END -->
						<const>1</const> si elle contient des <b>oeufs</b>.
						</em>
						<!-- BEGIN level2 -->
					</li>
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					</li>
					<!-- END -->
					<!-- END -->

					<li>
						<const>2</const> si elle contient des <b>cristaux</b>.
					</li>
				</ul>
				<p>La quantité de <var>ressources</var> contenues dans chaque cellule est également donnée, et peut changer au
					cours de la partie lorsque les fourmis <b>récoltent</b> les cellules.</p>

				Une cellule peut également contenir une <b>base</b>. Les fourmis des deux joueurs commenceront la partie sur ces
				bases.

				<center style="margin: 20px; display:flex; justify-content: space-evenly;">
					<div>
						<img src="/servlet/fileservlet?id=104656627297903"
							style="height: 113px; margin-bottom: 5px">
						<div>
							<em>Base bleue</em>
						</div>
					</div>
					<!-- BEGIN level2 level3 level4 -->
					<div>
						<img src="/servlet/fileservlet?id=104741602378901"
							style="height: 113px; margin-bottom: 5px">
						<div>
							<em>Oeufs</em>
						</div>
					</div>
					<!-- END -->
				</center>

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Les fourmis et les balises</h3>

				<p>Chaque joueur commence avec plusieurs fourmis sur
					<!-- BEGIN level1 -->
					<b>leurs base</b>.
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					<b>leurs bases</b>.
					<!-- END -->

					Les joueurs ne peuvent pas contrôler les fourmis directement, mais peuvent placer des <b>balises</b> pour les
					attirer.
				</p>

				<p>Les joueurs peuvent placer <b>autant</b> de balises par tour qu'ils le souhaitent, mais ne peuvent en placer
					qu'<b>une seule par cellule</b>.
				</p>

				<p>Quand ils placent une balise, les joueurs doivent leur donner une <var>puissance</var>. Ces puissances se
					comportent comme des <b>poids</b> qui déterminent la <b>proportion de fourmis</b> qui seront réparties sur
					chaque balise.</p>

				<p>Autrement dit, <b>plus</b> la balise sera <var>puissante</var>, plus le <b>percentage</b>
					de vos fourmis allant sur cette balise sera élevé.</p>

				<h3 style="font-size: 14px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Exemple</h3>

				<p>Dans l'exemple suivant, il y a 3 balises de <var>puissance</var>
					<const>2</const>,<const>1</const>, et <const>2</const>.
				</p>

				<div class="statement-example-container">

					<div class="statement-example statement-example-medium">
						<img src="/servlet/fileservlet?id=104656929719534" />
						<div class="legend">
							<div class="description">
								Les nombres blancs dans un diamant coloré représentent les fourmis. Ici, <const>10</const> fourmis seront
								réparties sur les balises.
							</div>
						</div>
					</div>
					<div class="statement-example statement-example-medium">
						<img src="/servlet/fileservlet?id=104656949815605" />
						<div class="legend">
							<div class="description">
								Les <const>10</const> fourmis vont se déplacer vers les 3 balises, en gardant les même proportions que
								les puissances des balises.
							</div>
						</div>
					</div>
				</div>

				<br>
				<p>Les fourmis feront de leur mieux pour utiliser le <b>plus court chemin</b> vers leur balise, en se déplaçant à
					une vitesse d'<b>une cellule par tour</b>.</p>

				<!--
<p><em>
Note: when calculating how many ants to dispatch to a beacon, the result will be rounded down to a minimum of <const>one</const>.</em></p>
-->

				<p>Entre chaque tour, les <b>balises existantes</b> sont désactivées et <b>retirées du jeu.</b></p>

				<p>
					Utilisez les balises pour placer vos fourmis de manière à créer une <b>chaîne de récolte</b> entre

					<!-- BEGIN level1 -->
					<b>votre base</b>
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					<b>vos bases</b>
					<!-- END -->

					et une <b>ressource</b>.
				</p>

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Chaîne de récolte</h3>

				<p>Afin de récolter des <b>cristaux</b> et marquer des points, il doit y avoir une <b>chaîne ininterrompue</b>
					de
					<b>cellules contenant vos fourmis</b> entre une ressources et
					<!-- BEGIN level1 -->
					<b>votre base</b>.
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					<b>vos bases</b>.
					<!-- END -->
				</p>

				<p>La quantité de cristaux récoltés par tour est égale au <b>maillon le plus faible</b> de la chaîne. Autrement
					dit, c'est la plus petite quantité de fourmis parmi les cellules qui composent la chaîne.</p>

				<center style="margin: 20px">
					<img src="/servlet/fileservlet?id=104661729944004"
						style="height: 226px; margin-bottom: 5px">
					<div>
					</div>
					<em>Ici, le joueur bleu récoltera <const>4</const> cristaux par tour.</em>
				</center>
				<br>




				<!-- BEGIN level3 level4 -->
				<p>Dans les jeux avec plusieurs bases par joueur, le jeu choisira la meilleure chaîne vers l'une ou l'autre de
					vos bases.</p>
				<!-- END -->

				<!-- BEGIN level2 -->
				<p style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->
					<!-- BEGIN level3 level4 -->
				<p>
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					Les chaîne de récolte fonctionne de la même façon pour les <b>oeufs</b>.
					<br><br>Récolter des oeufs créera autant de fourmis que de ressources récoltées. Les fourmis apparaîtront dans
					la base du joueur au début du tour suivant.
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					<!-- BEGIN level3 -->
				<div style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->
					Dans les parties où les joueurs ont plusieurs bases, les nouvelles fourmis apparaîtront dans <b>chaque
						base</b>, indépendamment de la base lié par la chaîne de récolte.<br><br>
					<!-- BEGIN level3 -->
				</div>
				<!-- END -->
				<!-- END -->
				<!-- BEGIN level2 -->
				</p>

				<!-- END -->
				<!-- BEGIN level3 level4 -->
				</p>
				<!-- END -->



				<p> <b>La récolte</b> est calculée indépendamment pour <b>chaque ressource</b>, et à chaque fois le jeu choisira
					automatiquement la <b>meilleure chaîne</b> de la cellule contenant la ressource jusqu'à la base.</p>


				<!-- BEGIN level3 level4 -->

				<!-- BEGIN level3 -->
				<div style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->

					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Attaquer les chaînes</h3>


					<p>Les chaînes de récolte d'un joueur peut être <b>interrompue</b> par les <b>chaînes d'attaque</b> du joueur
						adverse.</p>

					<p>Au moment du calul des chaînes de récolte, certaines cellules peuvent avoir des fourmis de chaque joueur.
						Pour chacune de ces cellules, la <b>chaîne d'attaque</b> de chaque joueur est calculée, et si l'un des
						joueur a une valeur plus faible, cette cellule ne sera pas prise en compte dans la chaîne de récolte.</p>

					La <b>valeur de la chaîne de récolte</b> pour une cellule donnée est le <b>maillon le plus faible</b> dans la
					chaîne entre cette cellule et l'une des bases du joueur.

					<h3 style="font-size: 14px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Exemple</h3>

					<div class="statement-example-container">

						<div class="statement-example statement-example-medium">
							<img src="/servlet/fileservlet?id=104743425035914" />
							<div class="legend">
								<div class="description">
									Les chaînes d'attaque pour une cellule contestée sont: <const>5</const> pour le joueur rouge et
									<const>3</const> pour le joueur bleu. <br>La chaîne de récolte n'est pas interrompue.
								</div>
							</div>
						</div>
						<div class="statement-example statement-example-medium">
							<img src="/servlet/fileservlet?id=104743443621336" />
							<div class="legend">
								<div class="description">
									Les chaînes d'attaque pour une cellule contestée sont: <const>5</const> pour le joueur rouge et
									<const>8</const> pour le joueur bleu. <br>La chaîne de récolte est interrompue.
								</div>
							</div>
						</div>
					</div>
					<!-- BEGIN level3 -->
				</div>
				<!-- END -->
				<!-- END -->

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Actions</h3>
				<p>
					A chaque tour les joueurs peuvent faire autant d'actions valides qu'ils le souhaitent parmi:
				</p>
				<ul>
					<li>
						<action>BEACON</action> <var>index</var> <var>strength</var>: place une balise de puissance
						<var>strength</var> sur la cellule
						<var>index</var>.
					</li>
					<li>
						<action>LINE</action> <var>index1</var> <var>index2</var> <var>strength</var>: place des balises le long
						d'un chemin entre la cellule <var>index1</var> et la cellule <var>index2</var>. Toutes les balises placées
						sont de puissance <var>strength</var>. Le chemin le plus court est choisi automatiquement.
					</li>
					<li>
						<action>WAIT</action>: ne rien faire.
					</li>
					<li>
						<action>MESSAGE</action> <var>text</var>. Affiche un texte sur votre côté du HUD.
					</li>
				</ul>

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Ordre des actions pour un tour</h3>


				<ol>
					<li>
						Les actions <action>LINE</action> sont calculées.
					</li>
					<li>
						Les actions <action>BEACON</action> sont calculées.
					</li>
					<li>
						Les fourmis se déplacent.
					</li>
					

					<!-- BEGIN level2 level3 level4 -->
					<!-- BEGIN level2 -->
					<li style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
						<!-- END -->
						<!-- BEGIN level3 level4 -->
					<li>
						<!-- END -->
						Les oeufs sont récoltés et les nouvelles fourmis apparaissent.
						<!-- BEGIN level2 -->
					</li>
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					</li>
					<!-- END -->
					<!-- END -->
					<li>
						Les cristaux sont récoltés et les points sont marqués.
					</li>
				</ol>
				<br>
				<!-- BEGIN level3 level4 -->
				<em>Note : lorsque deux joueurs récoltent à partir de la même ressource, ils recevront tous les deux la quantité prévue, peu importe s'il y assez de ressources restantes pour le soutenir.</em>
				<!-- END -->
				<br>

				<!-- Victory conditions -->
				<div class="statement-victory-conditions">
					<div class="icon victory"></div>
					<div class="blk">
						<div class="title">Conditions de Victoire</div>
						<div class="text">
							<ul style="padding-top:0; padding-bottom: 0;">
								<li>Vous avez plus de la moitié des <b>cristaux</b> sur la carte.</li>
								<li>Vous avez plus de <b>cristaux</b> que votre adversaire après <const>100</const> tours, ou plus de <b>fourmis</b> en cas d'égalité.
								</li>
							</ul>
						</div>
					</div>
				</div>
				<!-- Lose conditions -->
				<div class="statement-lose-conditions">
					<div class="icon lose"></div>
					<div class="blk">
						<div class="title">Conditions de Défaite</div>
						<div class="text">
							<ul style="padding-top:0; padding-bottom: 0;">
								Votre programme ne répond pas dans le temps imparti ou il fournit une commande non reconnue.
							</ul>
						</div>
					</div>
				</div>

			</div>

		</div>
		<br>
		<h3 style="font-size: 16px;
                font-weight: 700;
                padding-top: 20px;
        color: #838891;
                padding-bottom: 15px;">
			🐞 Conseils de débogage</h3>
		<ul>
			<li>Survolez une case pour voir plus d'informations sur celle-ci, y compris la <b>puissance</b> de la balise.</li>
			<li>Utilisez la commande <action>MESSAGE</action> pour afficher du texte sur votre côté du HUD.</li>
			<li>Cliquez sur la roue dentée pour afficher les options visuelles supplémentaires.</li>
			<li>Utilisez le clavier pour contrôler l'action : espace pour play / pause, les flèches pour avancer pas à pas
		</ul>
		<br>

	</div>

	<!-- EXPERT RULES 
		<div class="statement-section statement-expertrules">
			<h2>
				<span class="icon icon-expertrules">&nbsp;</span>
				<span>Technical Details</span>
			</h2>
			<div class="statement-expert-rules-content">
				<ul>
				</ul>
			</div>
		</div>
-->

	<!-- PROTOCOL -->
	<div class="statement-section statement-protocol">
		<h2 style="font-size: 20px;">
			<span class="icon icon-protocol">&nbsp;</span>
			<span>Protocole de Jeu</span>
		</h2>

		<!-- Protocol block -->
		<div class="blk">
			<div class="title">Entrées d'Initialisation</div>
			<span class="statement-lineno">Première ligne:</span> <var>numberOfCells</var> un entier pour le nombre de
			cellules de la carte.<br>
			<span class="statement-lineno">Les <var>numberOfCells</var> lignes suivantes :</span>
			les cellules, ordonnées par <var>index</var>. Chaque cellule est représentée par <const>8</const> entiers séparés
			par des espaces:
			<ul>
				<li><var>type</var>: <const>1</const> pour des oeufs, <const>2</const> pour des cristaux, <const>0</const>
					sinon.</li>
				<li><var>initialResources</var> pour la quantité de cristaux et d'oeuf dans la cellule.</li>
				<!-- BEGIN level1 -->
				<li>
					<const>6</const> <var>neigh</var> variables: <em>Ignorer pour cette ligue.</em>
				</li>
				<!-- END -->
				<!-- BEGIN level2 level3 level4 -->
				<!-- BEGIN level2 -->
				<li style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->
					<!-- BEGIN level3 level4 -->
				<li>
					<!-- END -->
					<const>6</const> <var>neigh</var> variables, une pour chaque <b>direction</b>, contenant l'index d'une cellule
					voisine ou <const>-1</const> s'il n'y a pas de voisine.
					<!-- BEGIN level2 -->
				</li>
				<!-- END -->
				<!-- BEGIN level3 level4 -->
				</li>
				<!-- END -->

				<!-- END -->
			</ul>
			<span class="statement-lineno">Ligne suivante :</span> un entier <var>numberOfBases</var>
			<!-- BEGIN level1 -->
			qui vaut <const>1</const> pour cette ligue.<br>
			<!-- END -->
			<!-- BEGIN level2 level3 level4 -->
			avec le nombre de bases de chaque joueur.<br>
			<!-- END -->
			<span class="statement-lineno">Ligne suivante :</span> <var>numberOfBases</var> pour les indices des cellules où
			il y a une <b>base alliée</b>.</span><br />
			<span class="statement-lineno">Ligne suivante :</span> <var>numberOfBases</var> pour les indices des cellules où
			il y a une <b>base ennemie</b>.</span>
		</div>
		<div class="blk">
			<div class="title">Entrées pour Un Tour de Jeu</div>
			<!-- BEGIN level4 -->
			<span class="statement-lineno">Ligne suivante :</span> <const>2</const> entiers <var>myScore</var> et <var>oppScore</var> pour la quantité de cristaux détenue par chaque joueur.<br>
			<!-- END -->
			<span class="statement-lineno">Les <var>numberOfCells</var> lignes suivantes :</span> une ligne par cellule,
			ordonnées par <var>index</var>. <const>3</const> entiers par cellule :<br>
			<ul>
				<li><var>resources</var> : la quantité de cristaux/oeufs sur la cellule.</li>
				<li><var>myAnts</var> : la quantité de fourmis que vous avez sur la cellule.
				</li>
				<li><var>oppAnts</var> : la quantité de fourmis que votre adversaire a sur la cellule.
			</ul>

			<div class="blk">
				<div class="title">Sortie</div>
				<div class="text">
					Toutes vos actions sur une ligne, séparées par un <action>;</action>
					<ul>
						<li>
							<action>BEACON</action> <var>index</var> <var>strength</var>.
							Place une balise qui dure un tour.
						</li>
						<li>
							<action>LINE</action> <var>index1</var> <var>index2</var> <var>strength</var>. Place des balises le long
							du chemin le plus court entre les deux cellules indiquées.
						</li>
						<li>
							<action>WAIT</action>. Ne fait rien.
						</li>
						<li>
							<action>MESSAGE</action> <var>text</var>. Affiche un texte sur votre côté du HUD.
						</li>
					</ul>
				</div>
			</div>

			<div class="blk">
				<div class="title">Contraintes</div>
				<div class="text">
					<!-- BEGIN level1 level2 -->
					<var>numberOfBases</var> = <const>1</const><br>
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					<const>1</const> ≤ <var>numberOfBases</var> ≤ <const>2</const><br>
					<var>numberOfCells</var> &lt; <const>100</const><br>
					<!-- END -->
					Temps de réponse par tour ≤ <const>100</const>ms<br>
					Temps de réponse pour le premier tour ≤ <const>1000</const>ms
				</div>
			</div>
		</div>
	</div>
	<!-- BEGIN level1 level2 -->
	<!-- LEAGUE ALERT -->
	<div style="color: #7cc576; 
                      background-color: rgba(124, 197, 118,.1);
                      padding: 20px;
                      margin-top: 10px;
                      text-align: left;">
		<div style="text-align: center; margin-bottom: 6px"><img
				src="//cdn.codingame.com/smash-the-code/statement/league_wood_04.png" /></div>

		<div style="text-align: center; font-weight: 700; margin-bottom: 6px;">
			Qu'est-ce qui m'attend dans les ligues suivantes ?
		</div>
		<ul>
			<!-- BEGIN level1 -->
			<li>Les oeufs seront disponibles.</li>
			<!-- END -->
			<li>Des cartes plus grandes seront disponibles.</li>
			<li>Les fourmis des deux joueurs pourront interagir.</li>
		</ul>
	</div>
	<!-- END -->


	<!-- STORY -->
	<div class="statement-story-background">
		<div class="statement-story-cover" style="background-size: cover;background-image: url(/servlet/fileservlet?id=103881564349131);">
			<div class="statement-story" style="min-height: 200px; position: relative">

				<div class="story-text">
					<h2><span style="color: #b3b9ad"><b>Kit de Démarrage</b></span></h2>
					Des IAs de base sont disponibles dans le
					<a style="color: #f2bb13; border-bottom-color: #f2bb13;" target="_blank" href="https://github.com/CodinGame/SpringChallenge2023/tree/main/starterAIs">kit de
						démarrage</a>.
					Elles peuvent
					vous aider à appréhender votre propre IA.
					<br><br><br>
					<h2><span style="color: #b3b9ad"><b>Code Dource</b></span></h2>
					Le code source de ce jeu est disponible <a style="color: #f2bb13; border-bottom-color: #f2bb13;" target="_blank"
						href="https://github.com/CodinGame/SpringChallenge2023">ici</a>.

				</div>
			</div>
		</div>
	</div>
</div>
<!-- SHOW_SAVE_PDF_BUTTON -->