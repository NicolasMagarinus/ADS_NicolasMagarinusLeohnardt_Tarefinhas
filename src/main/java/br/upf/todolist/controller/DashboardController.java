package br.upf.todolist.controller;

import br.upf.todolist.entity.StatusTarefa;
import br.upf.todolist.entity.Tarefa;
import br.upf.todolist.entity.Usuario;
import br.upf.todolist.facade.ResumoCategoriaTarefas;
import br.upf.todolist.facade.TarefaFacade;
import br.upf.todolist.facade.TarefaFiltro;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.primefaces.model.charts.pie.PieChartOptions;

@Named
@ViewScoped
public class DashboardController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private TarefaFacade tarefaFacade;

    @Inject
    private UsuarioLogado usuarioLogado;

    private long aFazer;
    private long fazendo;
    private long concluidas;
    private long atrasadas;
    private List<Tarefa> tarefasRecentes;
    private PieChartModel graficoStatus;
    private BarChartModel graficoCategorias;
    private boolean temTarefas;
    private boolean temCategorias;

    @PostConstruct
    public void carregar() {
        var usuario = usuarioLogado.getUsuario();
        Map<StatusTarefa, Long> totais = tarefaFacade.contarPorStatus(usuario);
        aFazer = totais.getOrDefault(StatusTarefa.A_FAZER, 0L);
        fazendo = totais.getOrDefault(StatusTarefa.FAZENDO, 0L);
        concluidas = totais.getOrDefault(StatusTarefa.CONCLUIDA, 0L);
        atrasadas = contarAtrasadas(usuario);
        List<ResumoCategoriaTarefas> porCategoria = tarefaFacade.contarPorCategoria(usuario);
        tarefasRecentes = tarefaFacade.buscar(new TarefaFiltro(), usuario, 0, 5, null, false);
        temTarefas = aFazer + fazendo + concluidas > 0;
        temCategorias = !porCategoria.isEmpty();
        graficoStatus = montarGraficoStatus(totais);
        graficoCategorias = montarGraficoCategorias(porCategoria);
    }

    private long contarAtrasadas(Usuario usuario) {
        TarefaFiltro filtro = new TarefaFiltro();
        filtro.setSoAtrasadas(true);
        return tarefaFacade.contar(filtro, usuario);
    }

    private PieChartModel montarGraficoStatus(Map<StatusTarefa, Long> totais) {
        List<String> rotulos = new ArrayList<>();
        List<Number> valores = new ArrayList<>();
        List<String> cores = new ArrayList<>();
        Map<StatusTarefa, String> paleta = new EnumMap<>(StatusTarefa.class);
        paleta.put(StatusTarefa.A_FAZER, "#f0ad4e");
        paleta.put(StatusTarefa.FAZENDO, "#42a5f5");
        paleta.put(StatusTarefa.CONCLUIDA, "#66bb6a");
        for (StatusTarefa status : StatusTarefa.values()) {
            rotulos.add(status.getDescricao());
            valores.add(totais.getOrDefault(status, 0L));
            cores.add(paleta.get(status));
        }
        PieChartDataSet conjunto = new PieChartDataSet();
        conjunto.setData(valores);
        conjunto.setBackgroundColor(cores);
        conjunto.setBorderColor(List.of("#ffffff", "#ffffff", "#ffffff"));
        conjunto.setBorderWidth(List.<Number>of(2, 2, 2));
        ChartData dados = new ChartData();
        dados.setLabels(rotulos);
        dados.addChartDataSet(conjunto);
        PieChartModel modelo = new PieChartModel();
        modelo.setData(dados);
        PieChartOptions opcoes = new PieChartOptions();
        opcoes.setResponsive(true);
        opcoes.setMaintainAspectRatio(false);
        modelo.setOptions(opcoes);
        return modelo;
    }

    private BarChartModel montarGraficoCategorias(List<ResumoCategoriaTarefas> resumos) {
        List<String> rotulos = new ArrayList<>();
        List<Number> valores = new ArrayList<>();
        for (ResumoCategoriaTarefas resumo : resumos) {
            rotulos.add(resumo.categoria());
            valores.add(resumo.quantidade());
        }
        BarChartDataSet conjunto = new BarChartDataSet();
        conjunto.setLabel("Tarefas");
        conjunto.setData(valores);
        conjunto.setBackgroundColor(Collections.nCopies(valores.size(), "rgba(0, 121, 107, 0.72)"));
        conjunto.setBorderColor(Collections.nCopies(valores.size(), "#00796b"));
        conjunto.setBorderWidth(1);
        ChartData dados = new ChartData();
        dados.setLabels(rotulos);
        dados.addChartDataSet(conjunto);
        BarChartModel modelo = new BarChartModel();
        modelo.setData(dados);
        BarChartOptions opcoes = new BarChartOptions();
        opcoes.setResponsive(true);
        opcoes.setMaintainAspectRatio(false);
        modelo.setOptions(opcoes);
        return modelo;
    }

    public long getTarefasAFazer() { return aFazer; }
    public long getFazendo() { return fazendo; }
    public long getConcluidas() { return concluidas; }
    public long getAtrasadas() { return atrasadas; }
    public List<Tarefa> getTarefasRecentes() { return tarefasRecentes; }
    public PieChartModel getGraficoStatus() { return graficoStatus; }
    public BarChartModel getGraficoCategorias() { return graficoCategorias; }
    public boolean isTemTarefas() { return temTarefas; }
    public boolean isTemCategorias() { return temCategorias; }
}
