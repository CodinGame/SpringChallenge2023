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
			This is a <b>league based</b> challenge.
		</p>
		<div class="statement-league-alert-content">
			For this challenge, multiple leagues for the same game are available. Once you have proven yourself
			against the
			first Boss, you will access a higher league and harder opponents will be available.<br>
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
		End the game with more crystal than your opponent.
		<center style="margin: 20px">
			<img src="/servlet/fileservlet?id=104654740443345"
				style="height: 113px; margin-bottom: 5px">
			<div>
				<em>Crystal</em>
			</div>
		</center>
		<p>The game takes place in a <b>lab</b>, in which two scientists in charge of <b>robot ants</b> are competing to
			find the most efficient way of gathering crystals.</p>
		However, the ants <b>cannot be controlled directly</b>. The ants will respond to the presence of <b>beacons</b>.

	</div>



	<!-- RULES -->
	<div class="statement-section statement-rules">
		<h2 style="font-size: 20px;">
			<span class="icon icon-rules">&nbsp;</span>
			<span>Rules</span>
		</h2>
		<div class="statement-rules-content">
			<div style="margin-bottom: 10px">
				The game is played in turns. On each turn, both players perform any number of actions simultaneously.

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					The Map</h3>

				<p>On each run, the map is <b>generated randomly</b> and is made up of <b>hexagonal cells</b>.</p>
				Each cell has an <b>index</b> and up to six neighbors. Each direction is labelled <const>0</const> to
				<const>5</const>.

				<center style="margin: 20px">
					<img src="/servlet/fileservlet?id=104672349644259"
						style="height: 226px; margin-bottom: 5px">
					<div>
						<em>Hex directions</em>
					</div>
				</center>
				<br>


				Each cell has a <b>type</b>, which indicates what the cell contains:
				<ul>
					<li>
						<const>0</const> if it does not contain a resource.
					</li>
					<!-- BEGIN level1 -->
					<li><em>
							<const>1</const> will appear in later leagues and can be ignored for now.
						</em></li>
					<!-- END -->

					<!-- BEGIN level2 level3 level4 -->
					<!-- BEGIN level2 -->
					<li style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
						<!-- END -->
						<!-- BEGIN level3 level4 -->
					<li>
						<!-- END -->
						<const>1</const> if it contains the <b>egg</b> resource.
						</em>
						<!-- BEGIN level2 -->
					</li>
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					</li>
					<!-- END -->
					<!-- END -->

					<li>
						<const>2</const> if it contains the <b>crystal</b> resource.
					</li>
				</ul>
				<p>The amount of <var>resources</var> contained in each cell is also given, and is subject to change
					during the game as the ants <b>harvest</b> cells.</p>

				A cell may also have a <b>base</b> on it. The players’ ants will start the game on these bases.

				<center style="margin: 20px; display:flex; justify-content: space-evenly;">
					<div>
						<img src="/servlet/fileservlet?id=104656627297903"
							style="height: 113px; margin-bottom: 5px">
						<div>
							<em>Blue base</em>
						</div>
					</div>
					<!-- BEGIN level2 level3 level4 -->
					<div>
						<img src="/servlet/fileservlet?id=104741602378901"
							style="height: 113px; margin-bottom: 5px">
						<div>
							<em>Egg</em>
						</div>
					</div>
					<!-- END -->
				</center>

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Ants & Beacons</h3>

				<p>Both players start with several ants placed on their
					<!-- BEGIN level1 -->
					<b>base</b>.
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					<b>bases</b>.
					<!-- END -->

					The players cannot move the ants
					directly but can place <b>beacons</b> to affect their movement.
				</p>

				<p>Players can place <b>any number</b> of beacons per turn but can only place <b>one each per cell</b>.
				</p>

				<p>When placing a beacon, players must give that beacon a <var>strength</var>. These beacon strengths
					act as <b>weights</b>, determining the <b>proportion of ants</b> that will be dispatched to each
					one.</p>

				<p>In other words, the <b>higher</b> the beacon <var>strength</var>, the greater the <b>percentage</b>
					of your ants that will be sent to that beacon.</p>

				<h3 style="font-size: 14px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Example</h3>

				<p>In the following example, there are three beacons of <var>strength</var>
					<const>2</const>,<const>1</const>, and <const>2</const>.
				</p>

				<div class="statement-example-container">

					<div class="statement-example statement-example-medium">
						<img src="/servlet/fileservlet?id=104656929719534" />
						<div class="legend">
							<div class="description">
								White numbers in a colored diamond represent the ants. Here, <const>10</const> ants in
								total will be
								dispatched to the beacons.
							</div>
						</div>
					</div>
					<div class="statement-example statement-example-medium">
						<img src="/servlet/fileservlet?id=104656949815605" />
						<div class="legend">
							<div class="description">
								The <const>10</const> ants will move to the three beacons, keeping the same
								proportions
								as the beacon strengths.
							</div>
						</div>
					</div>
				</div>

				<br>
				<p>The ants will do their best to take the <b>shortest paths</b> to their designated beacons, moving at
					a speed of <b>one cell per turn</b>. </p>

				<!--
<p><em>
Note: when calculating how many ants to dispatch to a beacon, the result will be rounded down to a minimum of <const>one</const>.</em></p>
-->

				<p>In between turns, the <b>existing beacons</b> are powered down and <b>removed from play.</b></p>

				<p>
					Use beacons to place your ants in such a way to create <b>harvesting chains</b> between your

					<!-- BEGIN level1 -->
					<b>base</b>
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					<b>bases</b>
					<!-- END -->

					and a <b>resource</b>.
				</p>

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Harvesting Chains</h3>

				<p>In order to harvest <b>crystal</b> and score points, there must be an <b>uninterrupted chain</b> of
					<b>cells containing your ants</b> between the resource and your
					<!-- BEGIN level1 -->
					<b>base</b>.
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					<b>bases</b>.
					<!-- END -->
				</p>

				<p>The amount of crystal harvested per turn is equal to the <b>weakest link</b> in the chain. In other
					words, it is the smallest amount of ants from the cells that make up the chain.</p>

				<center style="margin: 20px">
					<img src="/servlet/fileservlet?id=104661729944004"
						style="height: 226px; margin-bottom: 5px">
					<div>
					</div>
					<em>Here, the blue player will harvest <const>4</const> crystal per turn.</em>
				</center>
				<br>




				<!-- BEGIN level3 level4 -->
				<p>In games with multiple bases per player, the game will choose the best chain to either one of your
					bases.</p>
				<!-- END -->

				<!-- BEGIN level2 -->
				<p style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->
					<!-- BEGIN level3 level4 -->
				<p>
					<!-- END -->
					<!-- BEGIN level2 level3 level4 -->
					The harvesting chains work the same way for the <b>egg resource</b>.
					<br><br>Harvesting an egg cell will spawn as many ants as resources havested. The ants will spawn on the
					player’s base on the start of next turn.
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					<!-- BEGIN level3 -->
				<div style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->
					In games with multiple bases per player, the extra ants will spawn on <b>each base</b>, regardless of the base
					present in the harvest chain.<br><br>
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



				<p> <b>Harvesting</b> is calculated separately for <b>each resource</b>, and for each one the game will
					automatically choose the <b>best chain</b> from its cell to your base.</p>


				<!-- BEGIN level3 level4 -->

				<!-- BEGIN level3 -->
				<div style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->

					<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Attack Chains</h3>


					<p>A player’s harvest chains may be <b>broken</b> by their opponent’s <b>attack chains</b>.</p>

					<p>When computing harvest chains, some cells may have ants from both players. For each of these cells, the
						<b>attack chain</b> of both players is computed, and if one of the player has a lower value, this cell
						cannot be counted in the harvest chain.
					</p>

					The <b>attack chain value</b> for a given cell is the <b>weakest link</b> in a chain from that cell to one of
					the player’s bases.

					<h3 style="font-size: 14px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
						Example</h3>

					<div class="statement-example-container">

						<div class="statement-example statement-example-medium">
							<img src="/servlet/fileservlet?id=104743425035914" />
							<div class="legend">
								<div class="description">
									The attack chains for the contested cell are: <const>5</const> for the red player and <const>3</const>
									for the blue player. <br>The harvest chain is unbroken.
								</div>
							</div>
						</div>
						<div class="statement-example statement-example-medium">
							<img src="/servlet/fileservlet?id=104743443621336" />
							<div class="legend">
								<div class="description">
									The attack chains for the contested cell are: <const>5</const> for the red player and <const>8</const>
									for the blue player. <br>The harvest chain is broken.
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
					On each turn players can do any amount of valid actions, which include:
				</p>
				<ul>
					<li>
						<action>BEACON</action> <var>index</var> <var>strength</var>: place a beacon of strength
						<var>strength</var> on cell
						<var>index</var>.
					</li>
					<li>
						<action>LINE</action> <var>index1</var> <var>index2</var> <var>strength</var>: place beacons
						all
						along a path from <var>index1</var> to
						<var>index2</var>, all of strength <var>strength</var>. A shortest path is chosen
						automatically.
					</li>
					<li>
						<action>WAIT</action>: do nothing.
					</li>
					<li>
						<action>MESSAGE</action> <var>text</var>. Displays text on your side of the HUD.
					</li>
				</ul>

				<h3 style="font-size: 16px;
    font-weight: 700;
    padding-top: 20px;
    color: #838891;
    padding-bottom: 15px;">
					Action order for one turn</h3>


				<ol>
					<li>
						<action>LINE</action> actions are computed.
					</li>
					<li>
						<action>BEACON</action> actions are computed.
					</li>
					<li>
						Ants move.
					</li>
					
					<!-- BEGIN level2 level3 level4 -->
					<!-- BEGIN level2 -->
					<li style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
						<!-- END -->
						<!-- BEGIN level3 level4 -->
					<li>
						<!-- END -->
						Eggs are harvested and new ants spawn.
						<!-- BEGIN level2 -->
					</li>
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					</li>
					<!-- END -->
					<!-- END -->
				<li>
					Crystal is harvested and points are scored.
				</li>
				</ol>
				<br>
				<!-- BEGIN level3 level4 -->
					<em>Note: when two players harvest from the same resource, they will both receive the full expected amount regardless of whether there is enough resource to support it.</em>
					<!-- END -->
					<br>

				<!-- Victory conditions -->
				<div class="statement-victory-conditions">
					<div class="icon victory"></div>
					<div class="blk">
						<div class="title">Victory Conditions</div>
						<div class="text">
							<ul style="padding-top:0; padding-bottom: 0;">
								<li>You have over half of the total <b>crystal</b> on the map.</li>
								<li>You have more <b>crystal</b> than your opponent after <const>100</const> turns, or more <b>ants</b> if tied.
								</li>
							</ul>
						</div>
					</div>
				</div>
				<!-- Lose conditions -->
				<div class="statement-lose-conditions">
					<div class="icon lose"></div>
					<div class="blk">
						<div class="title">Defeat Conditions</div>
						<div class="text">
							<ul style="padding-top:0; padding-bottom: 0;">
								Your program does not provide a command in the allotted time or it provides an
								unrecognized command.
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
			🐞 Debugging tips</h3>
		<ul>
			<li>Hover over a tile to see extra information about it, including beacon
				<var>strength</var>.
			</li>
			<li>Use the <action>MESSAGE</action> command to display some text on your side of the HUD.
			</li>
			<li>Press the gear icon on the viewer to access extra display options.</li>
			<li>Use the keyboard to control the action: space to play/pause, arrows to step 1 frame at a
				time.
			</li>
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
			<span>Game Protocol</span>
		</h2>

		<!-- Protocol block -->
		<div class="blk">
			<div class="title">Initialization Input</div>
			<span class="statement-lineno">First line:</span> <var>numberOfCells</var> an integer for the amount of
			cells in the map.<br>
			<span class="statement-lineno">Next <var>numberOfCells</var> lines:</span>
			the cells, ordered by <var>index</var>. Each cell is represented by <const>8</const> space-separated
			integers:
			<ul>
				<li><var>type</var>: <const>1</const> for egg, <const>2</const> for crystal, <const>0</const>
					otherwise.</li>
				<li><var>initialResources</var> for the amount of crystal/egg here.</li>
				<!-- BEGIN level1 -->
				<li>
					<const>6</const> <var>neigh</var> variables: <em>Ignore for this league.</em>
				</li>
				<!-- END -->
				<!-- BEGIN level2 level3 level4 -->
				<!-- BEGIN level2 -->
				<li style="color: #7cc576; background: rgba(124, 197, 118, 0.1)">
					<!-- END -->
					<!-- BEGIN level3 level4 -->
				<li>
					<!-- END -->
					<const>6</const> <var>neigh</var> variables, one for each <b>direction</b>, containing the index
					of a
					neighboring cell or <const>-1</const> if there is no neighbor.
					<!-- BEGIN level2 -->
				</li>
				<!-- END -->
				<!-- BEGIN level3 level4 -->
				</li>
				<!-- END -->

				<!-- END -->
			</ul>
			<span class="statement-lineno">Next line:</span> one integer <var>numberOfBases</var>
			<!-- BEGIN level1 -->
			which equals <const>1</const> for this league.<br>
			<!-- END -->
			<!-- BEGIN level2 level3 level 4 -->
			containing the number of bases for each player.<br>
			<!-- END -->
			<span class="statement-lineno">Next line:</span> <var>numberOfBases</var> integers for the cell indices
			where a <b>friendly base</b> is present.</span><br />
			<span class="statement-lineno">Next line:</span> <var>numberOfBases</var> integers for the cell indices
			where an <b>opponent base</b> is present.</span>.
		</div>
		<div class="blk">
			<div class="title">Input for One Game Turn</div>
			<!-- BEGIN level4 -->
			<span class="statement-lineno">Next line:</span> <const>2</const> integers <var>myScore</var> and <var>oppScore</var> for the amount of crystal each player has.<br>
			<!-- END -->
			<span class="statement-lineno">Next <var>numberOfCells</var> lines:</span> one line per
			cell, ordered by <var>index</var>. <const>3</const> integers per cell:<br>
			<ul>
				<li><var>resources</var>: the amount of crystal/eggs on the cell.</li>
				<li><var>myAnts</var>: the amount of ants you have on the cell.
				</li>
				<li><var>oppAnts</var>: the amount of ants your opponent has on the cell.
			</ul>

			<div class="blk">
				<div class="title">Output</div>
				<div class="text">
					All your actions on one line, separated by a <action>;</action>
					<ul>
						<li>
							<action>BEACON</action> <var>index</var> <var>strength</var>.
							Places a beacon that lasts one turn.
						</li>
						<li>
							<action>LINE</action> <var>index1</var> <var>index2</var> <var>strength</var>. Places
							beacons along a path between the two provided cells.
						</li>
						<li>
							<action>WAIT</action>. Does nothing.
						</li>
						<li>
							<action>MESSAGE</action> <var>text</var>. Displays text on your side of the HUD.
						</li>
					</ul>
				</div>
			</div>

			<div class="blk">
				<div class="title">Constraints</div>
				<div class="text">
					<!-- BEGIN level1 level2 -->
					<var>numberOfBases</var> = <const>1</const><br>
					<!-- END -->
					<!-- BEGIN level3 level4 -->
					<const>1</const> ≤ <var>numberOfBases</var> ≤ <const>2</const><br>
					<var>numberOfCells</var> &lt; <const>100</const><br>
					<!-- END -->
					Response time per turn ≤ <const>100</const>ms<br>
					Response time for the first turn ≤ <const>1000</const>ms
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
			What is in store for me in the higher leagues?
		</div>
		<ul>
			<!-- BEGIN level1 -->
			<li>The egg resource will be available.</li>
			<!-- END -->
			<li>Larger maps will be available.</li>
			<li>Ants of opposing teams will interact.</li>
		</ul>
	</div>
	<!-- END -->


	<!-- STORY -->
	<div class="statement-story-background">
		<div class="statement-story-cover" style="background-size: cover; background-image: url(/servlet/fileservlet?id=103881564349131);">
			<div class="statement-story" style="min-height: 200px; position: relative">

				<div class="story-text">
					<h2><span style="color: #b3b9ad"><b>Starter Kit</b></span></h2>
					Starter AIs are available in the
					<a style="color: #f2bb13; border-bottom-color: #f2bb13;" target="_blank" href="https://github.com/CodinGame/SpringChallenge2023/tree/main/starterAIs">Starter
						Kit</a>.
					They can help you get started with your own bot. You can modify them to suit your own coding
					style or start completely
					from scratch.
					<br><br><br>
					<h2><span style="color: #b3b9ad"><b>Source code</b></span></h2>
					The game's source will be available <a style="color: #f2bb13; border-bottom-color: #f2bb13;" target="_blank"
						href="https://github.com/CodinGame/SpringChallenge2023">here</a>.

				</div>
			</div>
		</div>
	</div>
</div>
<!-- SHOW_SAVE_PDF_BUTTON -->