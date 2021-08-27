<?php
	class Canager
	{
		private $Page;
		
		private $Parent;
		
		private $Elements;
		
		public function __construct($Page, $Parent)
		{
			$this->Page = $Page;
			
			$this->Parent = $Parent;
			
			$this->Elements = array();
			
			$Query = mysql_query("SELECT id FROM f_categories WHERE parent = $Parent ORDER BY ordering");
			
			if (!mysql_num_rows($Query))
			{
				return;
			}
			
			$this->Page->GetFunctions()->Load('category');
			
			while ($Values = mysql_fetch_assoc($Query))
			{
				array_push($this->Elements, new Category($this->Page, $Values['id']));
			}
		}
		
		public function GetParent()
		{
			return $this->Parent;
		}
		
		public function GetElements()
		{
			return $this->Elements;
		}
		
		public function Display()
		{
			$this->Page->GetFunctions()->Load('player');
			
			if (count($this->Elements) > 0)
			{
				echo '<table class="forums" cellspacing="0" cellpadding="4">';
				
				echo '<tr>';
				
				echo '<th align="left" width="50%">';
				
				echo $this->Page->GetLanguage()->GetText('pages.forum.table.name');
				
				echo '</th>';
				
				echo '<th width="15%">';
				
				echo $this->Page->GetLanguage()->GetText('pages.forum.table.discuss');
				
				echo '</th>';
				
				echo '<th width="10%">';
				
				echo $this->Page->GetLanguage()->GetText('pages.forum.table.messages');
				
				echo '</th>';
				
				echo '<th align="right" width="25%">';
				
				echo $this->Page->GetLanguage()->GetText('pages.forum.table.last');
				
				echo '</th>';
				
				echo '</tr>';
					
				foreach ($this->Elements as $Category)
				{
					echo '<tr>';
					
					echo '<td>';
					
					echo '<div class="title">';
					
					echo '<a href="' . $this->Page->Link($Category->GetLink()) . '">' . $Category->GetName() . '</a>';
					
					echo '</div>';
					
					echo '<div class="shows">';
					
					echo $Category->GetDescription();
					
					$Category->LoadChilds();
					
					if (count($Category->GetChilds()) > 0)
					{
						foreach ($Category->GetChilds() as $Child)
						{
							echo '<br/><img src="includes/images/minion.gif" class="minion"/><a href="' . $this->Page->Link($Child->GetLink()) . '">' . $Child->GetName() . '</a>';
						}
					}
					
					echo '</div>';
					
					echo '</td>';
					
					echo '<td valign="middle" align="center">';
					
					echo count($Category->GetSubjects());
					
					echo '</td>';
					
					echo '<td valign="middle" align="center">';
					
					echo $Category->CountMessages();
					
					echo '</td>';
					
					echo '<td valign="middle" align="right">';
					
					if ($Category->GetLast() != null)
					{
						$Laster = new Player($this->Page, $Category->GetLast()->GetLast()->GetPoster());
						
						echo '<span class="subpos">';

						echo $this->Page->GetLanguage()->GetText('canager.in') . ' <a href="' . $this->Page->Link($Category->GetLast()->GetLink()) . '">' . $Category->GetLast()->GetTitle() . '</a> ';

						echo lcfirst($this->Page->GetLanguage()->GetText('pages.index.by')) . ' <a href="' . $this->Page->Link($Laster->GetLink()) . '">' . $Laster->GetColored() . '</a>, ';
						
						echo utf8_encode(ucfirst(strftime("%A %d %B " . utf8_decode($this->Page->GetLanguage()->GetText('pages.index.at')). " %H:%M", strtotime($this->Page->DecaleDate($Category->GetLast()->GetLast()->GetPosted())))));
						
						echo '</span>';
					}
					else
					{
						echo ' <span class="subpos">' . $this->Page->GetLanguage()->GetText('category.nomessage') . '</span>';
					}
					
					echo '</td>';
					
					echo '</tr>';
				}
					
				echo '</table>';
			}
		}
	}
?>