package graphapp.view.parts.edition.parts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TableRow {
    private String from;
    private String to;
    private int lowerBound;
    private int currentFlow;
    private int upperBound;
}
