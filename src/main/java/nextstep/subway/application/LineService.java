package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.exception.LineNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {

	private final StationService stationService;
	private final LineRepository lineRepository;

	public LineService(StationService stationService, LineRepository lineRepository) {
		this.stationService = stationService;
		this.lineRepository = lineRepository;
	}

	@Transactional
	public LineResponse saveLine(LineRequest lineRequest) {
		Line line = getLine(lineRequest);

		line = lineRepository.save(line);

		return LineResponse.of(line);
	}

	@Transactional
	public void updateLine(Long lineId, LineRequest lineRequest) {
		Line line = getLine(lineId);
		line.update(lineRequest.getName(), lineRequest.getColor());
	}

	@Transactional
	public void removeLine(Long id) {
		lineRepository.deleteById(id);
	}

	@Transactional
	public LineResponse addSections(long lineId, LineStationRequest request) {
		Line line = getLine(lineId);
		Station upStation = getStation(request.getUpStationId());
		Station downStation = getStation(request.getDownStationId());

		line.addSection(upStation, downStation, request.getDistance());

		return LineResponse.of(line);
	}

	public List<LineResponse> findAllLines() {
		return LineResponse.ofList(lineRepository.findAll());
	}

	public LineResponse findLine(Long lineId) {
		Line line = getLine(lineId);
		return LineResponse.of(line);
	}

	private Line getLine(Long lineId) {
		return lineRepository.findById(lineId)
			.orElseThrow(() -> new LineNotFoundException(lineId));
	}

	private Line getLine(LineRequest lineRequest) {
		Station upStation = getStation(lineRequest.getUpStationId());
		Station downStation = getStation(lineRequest.getDownStationId());

		return lineRequest.toLine(upStation, downStation);
	}

	private Station getStation(Long stationId) {
		return stationService.getStation(stationId);
	}
}
