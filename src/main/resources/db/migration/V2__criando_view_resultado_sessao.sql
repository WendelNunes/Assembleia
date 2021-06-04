CREATE VIEW resultado_sessao AS 
     SELECT s.id,
			(CURRENT_TIMESTAMP BETWEEN s.data_hora_inicio AND s.data_hora_fechamento) AS aberta,
    	    COUNT(CASE WHEN v.valor THEN 1 END) AS total_sim,
            COUNT(CASE WHEN v.valor = false THEN 1 END) AS total_nao
       FROM sessao s 
  LEFT JOIN voto v on v.id_sessao = s.id 
   GROUP BY s.id