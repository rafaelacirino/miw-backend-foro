package es.upm.miw.foro.api.converter;

import es.upm.miw.foro.api.dto.AnswerDto;
import es.upm.miw.foro.persistance.model.Answer;
import es.upm.miw.foro.persistance.model.Question;
import es.upm.miw.foro.persistance.model.User;
import es.upm.miw.foro.persistance.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AnswerMapper {

    private AnswerMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static AnswerDto toAnswerDto(Answer answer) {
        if (answer == null) {
            return null;
        }
        AnswerDto answerDto = new AnswerDto();
        populateDto(answer, answerDto);
        return answerDto;
    }

    public static Answer toEntity(AnswerDto answerDto, Question question, UserRepository userRepository) {
        if (answerDto == null) {
            return null;
        }
        Answer answer = new Answer();
        populateEntity(answer, answerDto, question, userRepository);
        return answer;
    }

    public static List<AnswerDto> toDtoList(List<Answer> answers) {
        return answers.stream()
                .map(AnswerMapper::toAnswerDto)
                .collect(Collectors.toList());
    }

    public static List<Answer> toEntityList(List<AnswerDto> answerDtos, Question question, UserRepository userRepository) {
        return answerDtos.stream()
                .map(dto -> AnswerMapper.toEntity(dto, question, userRepository))
                .collect(Collectors.toList());
    }

    public static void populateDto(Answer answer, AnswerDto answerDto) {
        answerDto.setId(answer.getId());
        answerDto.setContent(answer.getContent());
        answerDto.setAnswerAuthor(answer.getAuthor().getUserName());
        answerDto.setCreatedDate(answer.getCreationDate());
    }

    public static void populateEntity(Answer entity, AnswerDto answerDto, Question question, UserRepository userRepository) {
        entity.setId(answerDto.getId());
        entity.setContent(answerDto.getContent());
        entity.setQuestion(question);
        User author = userRepository.findByUserName(answerDto.getAnswerAuthor())
                .orElseThrow(() -> new RuntimeException("Author not found: " + answerDto.getAnswerAuthor()));        entity.setAuthor(author);
        entity.setCreationDate(answerDto.getCreatedDate());
    }
}
