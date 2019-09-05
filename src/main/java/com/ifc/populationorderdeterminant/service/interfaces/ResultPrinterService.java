package com.ifc.populationorderdeterminant.service.interfaces;

import com.ifc.populationorderdeterminant.dto.PopulationSequenceResult;

import java.util.List;

public interface ResultPrinterService {

    void print(List<PopulationSequenceResult> populationSequenceResults);

}
